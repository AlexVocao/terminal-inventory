package com.alex.inventory.controller;

import com.alex.inventory.dao.TerminalRepository;
import com.alex.inventory.entity.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory/terminals")
public class TerminalController {
    private final TerminalRepository terminalRepository;

    @Autowired
    public TerminalController(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    @GetMapping("")
    public String getTerminals(Model model) {
        model.addAttribute("terminals", terminalRepository.findAll());
        return "terminals"; // returns the view name to be rendered
    }

    @GetMapping("/add")
    public String showAddTerminalForm(Model model) {
        Terminal terminal = new Terminal();
        model.addAttribute("terminal", terminal);
        return "add-terminal-form";
    }

    @PostMapping("/save")
    public String saveTerminal(@ModelAttribute("terminal") Terminal terminal) {
        terminalRepository.save(terminal);
        return "redirect:/inventory/terminals";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateTerminalForm(@PathVariable("id") int id, Model model) {
        Terminal terminal = terminalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid terminal Id:" + id));
        model.addAttribute("terminal", terminal);
        return "add-terminal-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTerminal(@PathVariable("id") int id) {
        terminalRepository.deleteById(id);
        return "redirect:/inventory/terminals";
    }

}
