package com.bbolab.gaonna.api.v1.controller;

import com.bbolab.gaonna.api.v1.dto.quest.QuestCreateUpdateRequestDto;
import com.bbolab.gaonna.api.v1.dto.quest.QuestDetailResponseDto;
import com.bbolab.gaonna.api.v1.dto.quest.QuestListResponseItemDto;
import com.bbolab.gaonna.api.v1.dto.quest.QuestListResponseDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Collections;

import static com.bbolab.gaonna.api.v1.controller.MockFactoryUtil.createDummyQuestResponseDto;

@RestController
@RequestMapping("/v1/quest")
@RequiredArgsConstructor
public class QuestController {
    // TODO : Validator
    private final ModelMapper modelMapper;

    @ApiOperation(value = "Searching quest with Quest UUID")
    @ApiResponses({@ApiResponse(code = 200, message = "Success", response = QuestDetailResponseDto.class)})
    @GetMapping("{questId}")
    public ResponseEntity<QuestDetailResponseDto> get(@ApiParam(value = "ex) 72f92a8b-1866-4f08-bdf1-5c4826d0378b", required = true) @PathVariable String questId) {
        // find Quest by questId
        QuestDetailResponseDto dto = createDummyQuestResponseDto();
        dto.setQuestId(questId);
        return ResponseEntity.ok().body(dto);
    }

    @ApiOperation(value = "Searching quest", notes = "Search for quests inside a selected area on the map. You should pass top-right and bottom-left coordinates for searching.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success", response = QuestListResponseDto.class)})
    @GetMapping
    public ResponseEntity<QuestListResponseDto> list(@RequestParam double topLongitude, @RequestParam double topLatitude,
                                  @RequestParam double bottomLongitude, @RequestParam double bottomLatitude) {
        QuestListResponseItemDto info = modelMapper.map(createDummyQuestResponseDto(), QuestListResponseItemDto.class);

        QuestListResponseDto dto = QuestListResponseDto.builder().build();
        dto.setQuests(Collections.singletonList(info));
        dto.getQuests().forEach(d -> {
            d.setLongitude(Math.abs(topLongitude - bottomLongitude) / 2);
            d.setLatitude(Math.abs(topLatitude - bottomLatitude) / 2);
        });
        return ResponseEntity.ok().body(dto);
    }

    // TODO : Create 성공 이후 반환 데이터 정의 필요 : created(201)랑 URI만 반환할지, 생성된 데이터 DTO도 함께 반환할지
    @ApiOperation(value = "Create new quest")
    @ApiResponses({@ApiResponse(code = 201, message = "Success", response = QuestDetailResponseDto.class)})
    @PostMapping
    public ResponseEntity<QuestDetailResponseDto> create(@RequestBody QuestCreateUpdateRequestDto requestDto) {
//        QuestResponseDto dto = questService.createQuest(requestDto);
        QuestDetailResponseDto dto = createDummyQuestResponseDto();
        modelMapper.map(requestDto, dto);
        return ResponseEntity.created(URI.create("/v1/quest/" + dto.getQuestId())).body(dto);
    }

    // TODO : Update 성공 이후 반환 데이터 정의 필요 : ok(200)만 반환할지, 수정된 데이터 DTO도 함께 반환할지
    @ApiOperation(value = "Update quest")
    @ApiResponses({@ApiResponse(code = 200, message = "Success", response = QuestDetailResponseDto.class)})
    @PutMapping("{questId}")
    public ResponseEntity<QuestDetailResponseDto> update(@PathVariable String questId, @RequestBody QuestCreateUpdateRequestDto requestDto) {
        // TODO : need quest owner checking
        QuestDetailResponseDto questDetailResponseDto = createDummyQuestResponseDto();
        modelMapper.map(requestDto, questDetailResponseDto);
        questDetailResponseDto.setQuestId(questId);
        return ResponseEntity.ok().body(questDetailResponseDto);
    }

    @ApiOperation(value = "Delete quest")
    @ApiResponses({@ApiResponse(code = 200, message = "Success")})
    @DeleteMapping("{questId}")
    public ResponseEntity<Void> delete(@PathVariable String questId) {
        return ResponseEntity.ok().build();
    }

}
