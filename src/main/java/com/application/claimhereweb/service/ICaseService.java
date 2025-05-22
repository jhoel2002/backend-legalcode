package com.application.claimhereweb.service;

import com.application.claimhereweb.service.dto.ResponseCaseDTO;
import com.application.claimhereweb.service.dto.SaveCaseDTO;
//import com.application.claimhereweb.service.dto.SaveCaseUserDTO;

public interface ICaseService {
    public ResponseCaseDTO saveCaseAdministrator(SaveCaseDTO value_case, Long id_customer);

    // public ResponseCaseDTO saveCaseUser(SaveCaseUserDTO value_case);
}