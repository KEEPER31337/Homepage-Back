package keeper.project.homepage.admin.controller.equipment;

import static keeper.project.homepage.util.ClientUtil.getUserIP;

import javax.servlet.http.HttpServletRequest;
import keeper.project.homepage.admin.dto.equipment.EquipmentDto;
import keeper.project.homepage.admin.service.equipment.EquipmentService;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.common.service.util.AuthService;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.util.ImageCenterCropping;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.util.service.ThumbnailService.ThumbnailSize;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/v1/admin")
@RestController
@RequiredArgsConstructor
@Log4j2
@Secured({"ROLE_사서", "ROLE_회장"})
public class EquipmentController {

  private final EquipmentService equipmentService;
  private final ResponseService responseService;
  private final ThumbnailService thumbnailService;
  private final AuthService authService;

  @PostMapping(value = "/addition/equipment", consumes = "multipart/form-data", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public CommonResult addEquipment(@RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail, EquipmentDto equipmentDto, HttpServletRequest httpServletRequest){

    equipmentDto.setIpAddress(getUserIP(httpServletRequest));
    ThumbnailEntity thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCropping(),
        thumbnail, ThumbnailSize.LARGE, equipmentDto.getIpAddress());
    equipmentDto.setThumbnailId(thumbnailEntity.getId());

    equipmentService.addEquipment(equipmentDto);

    return responseService.getSuccessResult();
  }

  @DeleteMapping(value = "/equipment")
  public CommonResult deleteEquipment(String name, Long quantity) throws Exception {
    equipmentService.deleteEquipment(name, quantity);
    return responseService.getSuccessResult();
  }

  @PostMapping(value = "/addition/borrow_equipment")
  public CommonResult borrowEquipment(String name, Long quantity) throws Exception{
    Long borrowMemberId = authService.getMemberIdByJWT();

    equipmentService.borrowEquipment(name, quantity, borrowMemberId);
    return responseService.getSuccessResult();
  }

}
