/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.controller.dto;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 *
 * @author gean.carneiro
 * @param <T>
 */
public class PageResponseDto<T> {
    
    private List<T> pageContent;
    private int totalOfElements;
    
    public PageResponseDto(Page<T> page) {
        this.pageContent = page.getContent() == null ? Arrays.asList() : page.getContent();
        this.totalOfElements = (int) page.getTotalElements();
    }

    public List<T> getPageContent() {
        return pageContent;
    }

    public void setPageContent(List<T> pageContent) {
        this.pageContent = pageContent;
    }

    public int getTotalOfElements() {
        return totalOfElements;
    }

    public void setTotalOfElements(int totalOfElements) {
        this.totalOfElements = totalOfElements;
    }
    
    
    
}
