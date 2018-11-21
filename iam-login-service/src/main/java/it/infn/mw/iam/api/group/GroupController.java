/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.api.group;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.common.ErrorDTO;
import it.infn.mw.iam.api.common.GroupDTO;
import it.infn.mw.iam.api.common.GroupDTO.CreateGroup;
import it.infn.mw.iam.api.common.GroupDTO.UpdateGroup;
import it.infn.mw.iam.core.group.IamGroupService;
import it.infn.mw.iam.core.group.error.NoSuchGroupError;
import it.infn.mw.iam.persistence.model.IamGroup;

@RestController
public class GroupController {
  
  final IamGroupService groupService;
  final GroupDTOConverter converter;
  
  @Autowired
  public GroupController(IamGroupService groupService, GroupDTOConverter converter) {
    this.groupService = groupService;
    this.converter = converter;
  }
  
  private String buildValidationErrorMessage(BindingResult result) {
    StringBuilder sb = new StringBuilder("Invalid group: ");
    if (result.hasGlobalErrors()) {
      sb.append(result.getGlobalErrors().get(0).getDefaultMessage());
    } else {
      sb.append(result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(",")));
    }
    
    return sb.toString();
  }
  private void handleValidationError(BindingResult result) {
    if (result.hasErrors()) {
      throw new InvalidGroupError(buildValidationErrorMessage(result));
    }
  }
  
  @RequestMapping(value = "/iam/group", method = POST)
  @ResponseStatus(value = HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public GroupDTO createGroup(@RequestBody @Validated(CreateGroup.class) GroupDTO group, final BindingResult validationResult) {
    
    handleValidationError(validationResult);
    
    IamGroup entity = converter.entityFromDto(group);
    entity = groupService.createGroup(entity);
    return converter.dtoFromEntity(entity);
  }
  
  @RequestMapping(value = "/iam/group/{id}", method = PUT)
  @PreAuthorize("hasRole('ADMIN') or #iam.isGroupManager(#id)")
  public GroupDTO updateGroup(@PathVariable String id, @RequestBody @Validated(UpdateGroup.class) GroupDTO group, final BindingResult validationResult) {
    handleValidationError(validationResult);
    
    IamGroup entity = groupService.findByUuid(id).orElseThrow(()->NoSuchGroupError.forUuid(id));
    entity.setDescription(group.getDescription());
    entity = groupService.save(entity);
    return converter.dtoFromEntity(entity);  
  }
  
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidGroupError.class)
  @ResponseBody
  public ErrorDTO handleValidationError(InvalidGroupError e) {    
    return ErrorDTO.fromString(e.getMessage());
  }
  
}
