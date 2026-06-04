package com.pao.proiect.bank.service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class AuditService {

    private static volatile AuditService instance;
    private static final String AUDIT_FILE = "audit.csv";
    private final ReentrantLock lock = new ReentrantLock();

    private AuditService() {}

    public static AuditService getInstance() {
        if (instance == null) {
            synchronized (AuditService.class) {
                if (instance == null) {
                    instance = new AuditService();
                }
            }
        }
        return instance;
    }


    public void logAction(String numeActiune) {
        lock.lock();
        try {
            try (PrintWriter pw = new PrintWriter(new FileWriter(AUDIT_FILE, true))) {
                pw.println(numeActiune + "," + LocalDateTime.now());
            } catch (IOException e) {
                System.err.println("[AUDIT] Eroare scriere audit.csv: " + e.getMessage());
            }
        } finally {
            lock.unlock();
        }
    }
}
