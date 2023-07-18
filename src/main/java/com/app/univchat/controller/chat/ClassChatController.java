package com.app.univchat.controller.chat;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.dto.ClassRoomDto;
import com.app.univchat.service.chat.ClassChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "chatting-class", description = "클래스 채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatting/class")
public class ClassChatController {

    private final ClassChatService classChatService;

    @Tag(name = "chatting-class")
    @ApiOperation(value = "개설 수업 목록 조회")
    @GetMapping("{page}")
    public BaseResponse<List<ClassRoomDto>> getClassRoomList(@PathVariable int page){
        List<ClassRoomDto> result = classChatService.getClassRoomList(page);

        return BaseResponse.ok(BaseResponseStatus.SUCCESS, result);
    }
}
