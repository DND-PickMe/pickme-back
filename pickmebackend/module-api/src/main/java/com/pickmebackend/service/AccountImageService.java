package com.pickmebackend.service;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.exception.AccountImageException;
import com.pickmebackend.exception.UserNotFoundException;
import com.pickmebackend.properties.AccountImageProperties;
import com.pickmebackend.repository.account.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import static com.pickmebackend.error.ErrorMessage.*;

/**
 * Reference
 * https://github.com/spring-guides/gs-uploading-files
 */
@Service
@Slf4j
public class AccountImageService {

    private final Path rootLocation;

    private final JwtProvider jwtProvider;

    private final AccountRepository accountRepository;

    private final ErrorsFormatter errorsFormatter;

    public AccountImageService(AccountImageProperties accountImageProperties, JwtProvider jwtProvider, AccountRepository accountRepository,
                               ErrorsFormatter errorsFormatter) {
        this.rootLocation = Paths.get(accountImageProperties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        }catch(Exception e) {
            log.warn(e.getMessage());
        }
        this.jwtProvider = jwtProvider;
        this.accountRepository = accountRepository;
        this.errorsFormatter = errorsFormatter;
    }

    public ResponseEntity<?> saveImage(MultipartFile image, HttpServletRequest request) throws UserNotFoundException {
        String imageName = UUID.randomUUID().toString() + "_" +  StringUtils.cleanPath(image.getOriginalFilename());
        String extension = FilenameUtils.getExtension(imageName);
        try {
            if (image.isEmpty() || (imageName.contains(".."))
                    || (!"jpg".equals(extension) && !"jpeg".equals(extension) && !"png".equals(extension))) {
                return errorsFormatter.badRequest(INVALID_IMAGE.getValue());
            }

            try (InputStream inputStream = image.getInputStream()) {
                Path targetLocation = this.rootLocation.resolve(imageName);
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new AccountImageException("Failed to store file " + imageName, e);
        }
        String email = jwtProvider.getUsernameFromToken(request.getHeader(HttpHeaders.AUTHORIZATION).substring(7));
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        Account account = accountOptional.orElseThrow(UserNotFoundException::new);
        String newImagePath = UriComponentsBuilder
                .fromUriString("https://pickme-back.ga:8083")
                .path("/api/images/")
                .path(imageName)
                .toUriString();

        account.setImage(newImagePath);
        return new ResponseEntity<>(accountRepository.save(account), HttpStatus.CREATED);
    }

    public ResponseEntity<?> loadAsResource(String imageName, HttpServletRequest request) {
        try {
            Path imagePath = load(imageName);
            Resource resource = new UrlResource(imagePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = null;
                try {
                    contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                } catch (IOException e) {
                    return errorsFormatter.badRequest(INVALID_IMAGE.getValue());
                }
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return errorsFormatter.badRequest(CANNOT_READABLE_IMAGE.getValue());
            }
        } catch (MalformedURLException e) {
            return errorsFormatter.badRequest(INVALID_IMAGE.getValue());
        }
    }

    private Path load(String imageName) {
        return this.rootLocation.resolve(imageName).normalize();
    }
}
