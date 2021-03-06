package fi.metatavu.edelphi.pages.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CreateDelfoiBulletinPageController extends DelfoiPageController {

  public CreateDelfoiBulletinPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_BULLETINS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);

    List<DelfoiBulletin> bulletins = bulletinDAO.listByDelfoiAndArchived(delfoi, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<DelfoiBulletin>() {
      @Override
      public int compare(DelfoiBulletin o1, DelfoiBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);
    pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/createbulletin.jsp");
  }

}