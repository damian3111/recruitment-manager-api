package com.damian3111.recruitment_manager_api.configuration;

import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import com.damian3111.recruitment_manager_api.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.openapitools.model.JobDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final UserRepository userRepository;

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findUserEntityByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Converter<String, JobDto.EmploymentTypeEnum> employmentTypeConverter = new Converter<>() {
            @Override
            public JobDto.EmploymentTypeEnum convert(MappingContext<String, JobDto.EmploymentTypeEnum> context) {
                String source = context.getSource();
                if (source == null) {
                    return null;
                }
                try {
                    return JobDto.EmploymentTypeEnum.fromValue(source);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        };

        // Converter for experienceLevel (String to ExperienceLevelEnum)
        Converter<String, JobDto.ExperienceLevelEnum> experienceLevelConverter = new Converter<>() {
            @Override
            public JobDto.ExperienceLevelEnum convert(MappingContext<String, JobDto.ExperienceLevelEnum> context) {
                String source = context.getSource();
                if (source == null) {
                    return null;
                }
                try {
                    return JobDto.ExperienceLevelEnum.fromValue(source);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        };

        Converter<String, JobDto.EmploymentModeEnum> employmentModeConverter = new Converter<>() {
            @Override
            public JobDto.EmploymentModeEnum convert(MappingContext<String, JobDto.EmploymentModeEnum> context) {
                String source = context.getSource();
                if (source == null) {
                    return null;
                }
                try {
                    return JobDto.EmploymentModeEnum.fromValue(source);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        };

        Converter<String, Boolean> remoteConverter = new Converter<>() {
            @Override
            public Boolean convert(MappingContext<String, Boolean> context) {
                String employmentMode = context.getSource();
                return employmentMode != null && employmentMode.equalsIgnoreCase("Remote");
            }
        };

        Converter<BigDecimal, Double> bigDecimalToDoubleConverter = new Converter<>() {
            @Override
            public Double convert(MappingContext<BigDecimal, Double> context) {
                BigDecimal source = context.getSource();
                return source != null ? source.doubleValue() : null;
            }
        };

        PropertyMap<JobEntity, JobDto> jobMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                map().setCompany(source.getCompanyName()); // companyName -> company
                using(employmentTypeConverter).map(source.getEmploymentType()).setEmploymentType(null);
                using(experienceLevelConverter).map(source.getExperienceLevel()).setExperienceLevel(null);
                using(employmentModeConverter).map(source.getEmploymentMode()).setEmploymentMode(null);
                using(remoteConverter).map(source.getEmploymentMode()).setRemote(null);
                using(bigDecimalToDoubleConverter).map(source.getSalaryMin()).setSalaryMin(null);
                using(bigDecimalToDoubleConverter).map(source.getSalaryMax()).setSalaryMax(null);
            }
        };
        modelMapper.addMappings(jobMap);
        return modelMapper;
    }
}
