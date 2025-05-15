package com.application.claimhereweb.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
//import org.springframework.util.ResourceUtils;

//import com.application.claimhereweb.model.entity.Facture;
import com.application.claimhereweb.service.dto.ResponseFactureDTO;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class ReportGenerator {
    public byte[] exportToPdf(List<ResponseFactureDTO> list) throws JRException, FileNotFoundException {
        return JasperExportManager.exportReportToPdf(getReport(list));
    }

    public byte[] exportToXls(List<ResponseFactureDTO> list) throws JRException, FileNotFoundException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        SimpleOutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(byteArray);
        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setExporterInput(new SimpleExporterInput(getReport(list)));
        exporter.setExporterOutput(output);
        exporter.exportReport();
        output.close();
        return byteArray.toByteArray();
    }

    private JasperPrint getReport(List<ResponseFactureDTO> list) throws FileNotFoundException, JRException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("petsData", new JRBeanCollectionDataSource(list));

        // Cargar el archivo .jrxml desde el classpath
        InputStream jrxmlInputStream = getClass().getClassLoader().getResourceAsStream("reports/Facture.jrxml");
        if (jrxmlInputStream == null) {
            throw new FileNotFoundException("El archivo Facture.jrxml no se encuentra en el classpath.");
        }

        // Compilar el reporte utilizando el archivo .jrxml cargado
        JasperPrint report = JasperFillManager.fillReport(
                JasperCompileManager.compileReport(jrxmlInputStream),
                params, new JREmptyDataSource());

        return report;
    }
}
