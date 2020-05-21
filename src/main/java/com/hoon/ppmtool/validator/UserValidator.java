package com.hoon.ppmtool.validator;

import com.hoon.ppmtool.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            errors.rejectValue("password", "Length", "Password must be at least 6 characters.");
            return;
        }
        if (!user.getPassword().equals(user.getConfirmpassword())) {
            errors.rejectValue("confirmpassword", "Match", "Passwords must match.");
        }
    }
}
