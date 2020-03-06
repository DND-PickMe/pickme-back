package com.pickmebackend.service;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.enterprise.EnterpriseFilterRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseResponseDto;
import com.pickmebackend.domain.dto.enterprise.SuggestionDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.repository.enterprise.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;

    private final ErrorsFormatter errorsFormatter;

    public EnterpriseResponseDto loadProfile(Account account) {
        Enterprise enterprise = account.getEnterprise();
        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(enterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(account.getEmail());

        return enterpriseResponseDto;
    }

    public EnterpriseResponseDto loadEnterprise(Account account) {
        Enterprise enterprise = account.getEnterprise();
        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(enterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(account.getEmail());

        return enterpriseResponseDto;
    }

    public Page<Enterprise> loadEnterprisesWithFilter(EnterpriseFilterRequestDto enterpriseFilterRequestDto, Pageable pageable) {
        return this.enterpriseRepository.filterEnterprise(enterpriseFilterRequestDto, pageable);
    }

    public EnterpriseResponseDto saveEnterprise(EnterpriseRequestDto enterpriseRequestDto) {
        Account account = modelMapper.map(enterpriseRequestDto, Account.class);
        account.setPassword(passwordEncoder.encode(enterpriseRequestDto.getPassword()));
        account.setNickName(enterpriseRequestDto.getName());
        account.setCreatedAt(LocalDateTime.now());
        account.setUserRole(UserRole.ENTERPRISE);

        Enterprise enterprise = modelMapper.map(enterpriseRequestDto, Enterprise.class);
        Enterprise savedEnterprise = this.enterpriseRepository.save(enterprise);

        account.setEnterprise(savedEnterprise);
        Account savedAccount = this.accountRepository.save(account);

        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(savedEnterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(savedAccount.getEmail());
        enterpriseResponseDto.setAccount(savedAccount);

        return enterpriseResponseDto;
    }

    public EnterpriseResponseDto updateEnterprise(Account account, EnterpriseRequestDto enterpriseRequestDto) {
        modelMapper.map(enterpriseRequestDto, account);
        account.setPassword(passwordEncoder.encode(enterpriseRequestDto.getPassword()));
        account.setNickName(enterpriseRequestDto.getName());

        Optional<Enterprise> enterpriseOptional = this.enterpriseRepository.findById(account.getEnterprise().getId());
        Enterprise enterprise = enterpriseOptional.get();
        modelMapper.map(enterpriseRequestDto, enterprise);
        Enterprise modifiedEnterprise = this.enterpriseRepository.save(enterprise);

        account.setEnterprise(modifiedEnterprise);
        Account modifiedAccount = this.accountRepository.save(account);

        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(modifiedEnterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(modifiedAccount.getEmail());
        enterpriseResponseDto.setAccount(modifiedAccount);

        return enterpriseResponseDto;
    }

    public EnterpriseResponseDto deleteEnterprise(Account account) {
        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(account, EnterpriseResponseDto.class);
        this.accountRepository.delete(account);

        return enterpriseResponseDto;
    }

    public boolean isDuplicatedEnterprise(EnterpriseRequestDto enterpriseRequestDto) {
        return this.accountRepository.findByEmail(enterpriseRequestDto.getEmail()).isPresent();
    }

    public boolean isNonEnterprise(Long enterpriseId) {
        return !this.accountRepository.findById(enterpriseId).isPresent();
    }

    public ResponseEntity<?> sendSuggestion(Long accountId, Account currentUser) throws MessagingException {
        Optional<Account> workerOptional = accountRepository.findById(accountId);
        if (!workerOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USER_NOT_FOUND), HttpStatus.BAD_REQUEST);
        }

        Enterprise enterprise = currentUser.getEnterprise();
        Account worker = workerOptional.get();
        String content = this.build(enterprise, worker);

        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(worker.getEmail());
            mimeMessageHelper.setSubject("[PickMe] " + enterprise.getName() + "에서 채용 제안 메일이 도착했습니다!");
            mimeMessageHelper.setText(content, true);
        };
        this.javaMailSender.send(mimeMessagePreparator);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String build(Enterprise enterprise, Account currentUser) {
        Context context = new Context();
        context.setVariable("enterprise", new SuggestionDto(enterprise, currentUser));
        return templateEngine.process("html/email.html", context);
    }
}
