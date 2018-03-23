package io.pivotal.workshop.codesnippetmanagerjdbc.controllers;

import io.pivotal.workshop.codesnippetmanagerjdbc.models.NewSnippet;
import io.pivotal.workshop.codesnippetmanagerjdbc.models.SnippetInfo;
import io.pivotal.workshop.codesnippetmanagerjdbc.models.SnippetRecord;
import io.pivotal.workshop.codesnippetmanagerjdbc.presenters.SnippetPresenter;
import io.pivotal.workshop.codesnippetmanagerjdbc.repositories.SnippetRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/snippets")
public class SnippetController {
    private final SnippetRepository snippetRepository;
    private final SnippetPresenter snippetPresenter;

    public SnippetController(SnippetRepository snippetRepository, SnippetPresenter snippetPresenter) {
        this.snippetRepository = snippetRepository;
        this.snippetPresenter = snippetPresenter;
    }

    @GetMapping
    public List<SnippetInfo> snippets() {
        return snippetRepository.findAll()
                .stream()
                .map(snippetPresenter::present)
                .collect(toList());
    }

    @GetMapping("/{id}")
    public SnippetInfo snippet(@PathVariable("id") String id) {
        SnippetRecord record = snippetRepository.findOne(id);
        return snippetPresenter.present(record);
    }

    @GetMapping("/{startDate}/{endDate}")
    public List<SnippetInfo> snippetsByDate(@PathVariable("startDate") String startDate,
                                            @PathVariable("endDate") String endDate) {
        return snippetRepository.findAllByDates(startDate, endDate)
                .stream()
                .map(snippetPresenter::present)
                .collect(toList());
    }

    @PostMapping
    public ResponseEntity<SnippetInfo> add(@RequestBody NewSnippet newSnippetFields) {
        SnippetRecord savedSnippetRecord = snippetRepository.save(newSnippetFields);
        SnippetInfo savedSnippetInfo = snippetPresenter.present(savedSnippetRecord);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(buildSnippetUri(savedSnippetInfo));
        return new ResponseEntity<>(savedSnippetInfo, httpHeaders, HttpStatus.CREATED);
    }

    private URI buildSnippetUri(SnippetInfo snippet) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest().path("/" +
                        snippet.id)
                .buildAndExpand().toUri();
    }
}
