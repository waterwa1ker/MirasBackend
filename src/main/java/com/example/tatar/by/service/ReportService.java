package com.example.tatar.by.service;

import com.example.tatar.by.model.Report;
import com.example.tatar.by.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public Report findById(int id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Report report) {
        reportRepository.save(report);
    }

    @Transactional
    public void delete(Report report) { reportRepository.delete(report);}
}