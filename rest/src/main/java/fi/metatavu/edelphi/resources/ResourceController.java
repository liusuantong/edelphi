package fi.metatavu.edelphi.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.panels.PanelController;

/**
 * Controller for resources
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ResourceController {

  @Inject
  private PanelController panelController;
  
  @Inject
  private PanelDAO panelDAO;
  
  /**
   * Returns a panel for given resource
   * 
   * @param resource resource
   * @return panel or null if not found
   */
  public Panel getResourcePanel(Resource resource) {
    Folder resourceRootFolder = getResourceRootFolder(resource);
    return panelDAO.findByRootFolder(resourceRootFolder);
  }
  
  /**
   * Returns whether folder is archived
   * 
   * @param folder folder
   * @return whether folder is archived
   */
  public boolean isFolderArchived(Folder folder) {
    if (folder.getArchived()) {
      return true;
    }
    
    Folder parentFolder = folder.getParentFolder();
    if (parentFolder != null) {
      return isFolderArchived(parentFolder);
    } else {
      Panel panel = panelDAO.findByRootFolder(parentFolder);
      if (panel != null) {
        return panelController.isPanelArchived(panel);
      }
    }

    return false;
  }
  
  /**
   * Returns a root folder of given resource
   * 
   * @param resource resource
   * @return root folder
   */
  private Folder getResourceRootFolder(Resource resource) {
    if (resource.getParentFolder() == null) {
      return (Folder) resource;
    }
    
    return getResourceRootFolder(resource.getParentFolder());
  }
  
}