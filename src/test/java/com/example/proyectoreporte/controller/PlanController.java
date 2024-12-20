/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.proyectoreporte.controller;

import com.example.proyectoreporte.service.PlanService;
import com.example.proyectoreporte.model.Plan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletResponse;

// Importaciones PDF


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.IOException;
import java.util.List;



/**
 *
 * @author PC
 */
@Controller
@RequestMapping("/planes")
public class PlanController {
    
    private final PlanService service;

    public PlanController(PlanService planService) {
        this.service = planService;
    }

    @GetMapping
    public String listarPlanes(Model model) {
        model.addAttribute("planes", this.service.listarTodos());
        return "planes";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("plan", new Plan());
        return "formulario_plan";
    }

    @PostMapping
    public String guardarPlan(@ModelAttribute Plan plan) {
        this.service.guardar(plan);
        return "redirect:/planes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("plan", this.service.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("ID invalido " + id)));
        return "formulario_plan";
    }

    @GetMapping("/reporte/pdf")
    public void generarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=planes_reporte.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        document.add(new Paragraph("Reporte de Planes").setBold().setFontSize(18));

        Table table = new Table(3) {};
        table.addCell("ID");
        table.addCell("Nombre");
        table.addCell("Descripcion");

        List<Plan> planes = this.service.listarTodos();
        for (Plan plan : planes) {
            table.addCell(plan.getId().toString());
            table.addCell(plan.getNombre());
            table.addCell(plan.getDescripcion());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/reporte/excel")
    public void generarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=planes_reporte.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Planes");

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"ID", "Nombre", "Descripcion"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        List<Plan> planes = this.service.listarTodos();
        int rowIndex = 1;
        for (Plan plan : planes) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(plan.getId());
            row.createCell(1).setCellValue(plan.getNombre());
            row.createCell(2).setCellValue(plan.getDescripcion());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
