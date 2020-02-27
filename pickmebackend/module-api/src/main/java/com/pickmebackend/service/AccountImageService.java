package com.pickmebackend.service;

import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.exception.AccountImageException;
import com.pickmebackend.properties.AccountImageProperties;
import com.pickmebackend.repository.AccountRepository;
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
import static com.pickmebackend.error.ErrorMessageConstant.*;

/**
 * Reference
 * https://github.com/spring-guides/gs-uploading-files
 */
@Service
public class AccountImageService {

    private final Path rootLocation;

    private final JwtProvider jwtProvider;

    private final AccountRepository accountRepository;

    public AccountImageService(AccountImageProperties accountImageProperties, JwtProvider jwtProvider, AccountRepository accountRepository) {
        this.rootLocation = Paths.get(accountImageProperties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        this.jwtProvider = jwtProvider;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<?> saveImage(MultipartFile image, HttpServletRequest request) {
        String imageName = UUID.randomUUID().toString() + "_" +  StringUtils.cleanPath(image.getOriginalFilename());
        String extension = FilenameUtils.getExtension(imageName);
        try {
            if (image.isEmpty()) {
                return new ResponseEntity<>(new ErrorMessage(INVALIDIMAGE), HttpStatus.BAD_REQUEST);
            }
            if (imageName.contains("..")) {
                return new ResponseEntity<>(new ErrorMessage(INVALIDIMAGE), HttpStatus.BAD_REQUEST);
            }

            if (!"jpg".equals(extension) && !"jpeg".equals(extension) && !"png".equals(extension)) {
                return new ResponseEntity<>(new ErrorMessage(INVALIDIMAGE), HttpStatus.BAD_REQUEST);
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
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Account account = accountOptional.get();
        String newImagePath = UriComponentsBuilder
                .fromUriString("https://pickme-back.ga")
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
                    return new ResponseEntity<>(new ErrorMessage(INVALIDIMAGE), HttpStatus.BAD_REQUEST);
                }
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return new ResponseEntity<>(CANNOTREADABLEIMAGE, HttpStatus.BAD_REQUEST);
            }
        } catch (MalformedURLException e) {
            return new ResponseEntity<>(INVALIDIMAGE, HttpStatus.BAD_REQUEST);
        }
    }

    private Path load(String imageName) {
        return this.rootLocation.resolve(imageName).normalize();
    }
}
