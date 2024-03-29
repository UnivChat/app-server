package com.app.univchat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@ApiModel(value = "토큰 발급 Dto")
public class JwtDto {

    @ApiModelProperty(name = "인증 타입", example = "Bearer")
    private String grantType;

    @ApiModelProperty(name = "accessToken", example = "eyJhdsfasdfafsda.sdafSDFsdf13SDFSDfsfSFSDF5sf")
    private String accessToken;

    @ApiModelProperty(name = "refreshToken", example = "eyJhdsfasdfafsda.sdafSDFsdf13SDFSDfsfSFSDF5sf")
    private String refreshToken;

}
