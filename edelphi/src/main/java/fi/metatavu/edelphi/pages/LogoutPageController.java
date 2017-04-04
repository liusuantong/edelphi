package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public class LogoutPageController extends PageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    RequestUtils.logoutUser(pageRequestContext);
    
    pageRequestContext.setRedirectURL(pageRequestContext.getRequest().getContextPath() + "/index.page");
  }
}
