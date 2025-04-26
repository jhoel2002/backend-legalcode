package com.application.claimhereweb.service;

import java.io.FileNotFoundException;

import com.application.claimhereweb.service.dto.ResponseFactureDTO;
import com.application.claimhereweb.service.dto.SaveFactureDTO;

import net.sf.jasperreports.engine.JRException;

public interface IFactureService {

    public ResponseFactureDTO saveFacture(SaveFactureDTO value_case, Long id_customer);

    byte[] exportPdf() throws JRException, FileNotFoundException;

    byte[] exportXls() throws JRException, FileNotFoundException;
}