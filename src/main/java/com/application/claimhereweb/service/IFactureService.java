package com.application.claimhereweb.service;

import com.application.claimhereweb.service.dto.ResponseFactureDTO;
import com.application.claimhereweb.service.dto.SaveFactureDTO;

public interface IFactureService {

    public ResponseFactureDTO saveFacture(SaveFactureDTO value_case, Long id_customer);
}
