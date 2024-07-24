package com.project.scholarship.service;

import com.project.scholarship.entity.Student;
import com.project.scholarship.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findByScholarshipName(String scholarshipName) {
        return studentRepository.findByScholarshipName(scholarshipName);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public void save(Student student) {
        studentRepository.save(student);
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }
    public Optional<Student> findByRegistrationNumberAndDob(String registrationNumber, LocalDate dob) {
        return studentRepository.findByRegistrationNumberAndDob(registrationNumber, dob);
    }
    public List<String> findDistinctScholarshipNames() {
        return studentRepository.findDistinctScholarshipNames();
    }

    public List<String> findDistinctDepartments() {
        return studentRepository.findDistinctDepartments();
    }

    public long countTotalStudents() {
        return studentRepository.count();
    }

    public long countTotalDepartments() {
             return studentRepository.findDistinctDepartments().size();
    }

    public long countStudentsAvailedScholarship() {
        return studentRepository.countByScholarshipNameIsNotNull();
    }

    public List<String> getDistinctScholarshipNames() {
        return studentRepository.findDistinctScholarshipNames();
    }

    public List<String> getDistinctDepartments() {
        return studentRepository.findDistinctDepartments();
    }



    public long countByDepartment(String department) {
        return studentRepository.countByDepartment(department);
    }

    public long countByScheme(String scheme) {
        return studentRepository.countByScheme(scheme);
    }

    public void save(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<Student> students = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) { // Skip header row
                    continue;
                }
                Student student = new Student();
                student.setRegistrationNumber(getStringCellValue(row.getCell(0)));
                student.setName(getStringCellValue(row.getCell(1)));
                student.setDob(getDateCellValue(row.getCell(2)));
                student.setEmail(getStringCellValue(row.getCell(3)));
                student.setDepartment(getStringCellValue(row.getCell(4)));
                student.setYearOfStudy(getStringCellValue(row.getCell(5)));
                student.setYearOfJoining(getStringCellValue(row.getCell(6)));
                student.setAcademicYear(getStringCellValue(row.getCell(7)));
                student.setCommunity(getStringCellValue(row.getCell(8)));
                student.setScholarshipName(getStringCellValue(row.getCell(9)));
                student.setScholarshipType(getStringCellValue(row.getCell(10)));
                student.setMobileNumber(getStringCellValue(row.getCell(11)));
                student.setScholarshipAmount(getDoubleCellValue(row.getCell(12)));
                student.setStatus(getStringCellValue(row.getCell(13)));
                students.add(student);
            }
            studentRepository.saveAll(students);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return null;
        }
    }

    private int getIntCellValue(Cell cell) {
        if (cell == null) {
            return 0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            return Integer.parseInt(cell.getStringCellValue());
        } else {
            return 0;
        }
    }

    private double getDoubleCellValue(Cell cell) {
        if (cell == null) {
            return 0.0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            return Double.parseDouble(cell.getStringCellValue());
        } else {
            return 0.0;
        }
    }

    private LocalDate getDateCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            return LocalDate.parse(cell.getStringCellValue(), DATE_FORMATTER);
        } else {
            return null;
        }
    }



}
