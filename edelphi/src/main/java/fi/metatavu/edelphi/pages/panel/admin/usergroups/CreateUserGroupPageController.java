package fi.metatavu.edelphi.pages.panel.admin.usergroups;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.panel.PanelPageController;

public class CreateUserGroupPageController extends PanelPageController {

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

}
