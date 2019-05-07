package fi.metatavu.edelphi.rest;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.ejb3.annotation.SecurityDomain;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.comments.QueryQuestionCommentController;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.mqtt.MqttController;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.permissions.DelfoiActionName;
import fi.metatavu.edelphi.permissions.PermissionController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryQuestionAnswer;
import fi.metatavu.edelphi.queries.QueryQuestionAnswerData;
import fi.metatavu.edelphi.queries.QueryQuestionLive2dAnswerData;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.rest.api.PanelsApi;
import fi.metatavu.edelphi.rest.model.QueryQuestionComment;
import fi.metatavu.edelphi.rest.mqtt.QueryQuestionAnswerNotification;
import fi.metatavu.edelphi.rest.mqtt.QueryQuestionCommentNotification;
import fi.metatavu.edelphi.rest.translate.QueryPageTranslator;
import fi.metatavu.edelphi.rest.translate.QueryQuestionAnswerTranslator;
import fi.metatavu.edelphi.rest.translate.QueryQuestionCommentTranslator;
import fi.metatavu.edelphi.users.UserController;

/**
 * Panel REST Services
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Path("/panels")
@Stateful
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
@SecurityDomain("keycloak")
public class PanelRESTService extends AbstractApi implements PanelsApi {

  private static final String QUERY_QUESTION_COMMENTS_MQTT_CHANNEL = "queryquestioncomments";
  private static final String QUERY_QUESTION_ANSWERS_MQTT_CHANNEL = "queryquestionanswers";

  @Inject
  private PanelController panelController;

  @Inject
  private QueryController queryController;

  @Inject  
  private UserController userController;

  @Inject
  private MqttController mqttController;

  @Inject
  private QueryQuestionCommentController queryQuestionCommentController;

  @Inject
  private QueryQuestionCommentTranslator queryQuestionCommentTranslator;

  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryQuestionAnswerTranslator queryQuestionAnswerTranslator;
  
  @Inject
  private QueryPageController queryPageController;
  
  @Inject
  private QueryPageTranslator queryPageTranslator;
  
  @Inject
  private PermissionController permissionController;
  
  @Override
  @RolesAllowed("user")
  public Response createQueryQuestionComment(QueryQuestionComment body, Long panelId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }

    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment parentComment = body.getParentId() != null ? queryQuestionCommentController.findQueryQuestionCommentById(body.getParentId()) : null;
    if (body.getParentId() != null && parentComment == null) {
      return createBadRequest(String.format("Invalid parent id %d", body.getParentId()));
    }
    
    QueryPage queryPage = queryController.findQueryPageById(body.getQueryPageId());
    if (queryPage == null) {
      return createBadRequest(String.format("Invalid query page id %d", body.getQueryPageId()));
    }
    
    QueryReply queryReply = queryController.findQueryReplyById(body.getQueryReplyId());
    if (queryReply == null) {
      return createBadRequest(String.format("Invalid query reply id %d", body.getQueryPageId()));
    }

    if (parentComment != null && !parentComment.getQueryPage().getId().equals(queryPage.getId())) {
      return createBadRequest(String.format("Invalid parent id %d", body.getParentId()));
    }

    Query pageQuery = queryPage.getQuerySection().getQuery();
    Query replyQuery = queryReply.getQuery();
    
    if (!pageQuery.getId().equals(replyQuery.getId())) {
      return createBadRequest("Reply and page mismatch");
    }
    
    if (!queryController.isPanelsQuery(pageQuery, panel)) {
      return createBadRequest("Panel and query mismatch");
    }
        
    String contents = body.getContents();
    Boolean hidden = body.isisHidden();
    Date created = new Date(System.currentTimeMillis());
    User creator = getLoggedUser();
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.createQueryQuestionComment(queryReply, queryPage, parentComment, contents, hidden, creator, created);

    publishCommentMqttNotification(QueryQuestionCommentNotification.Type.CREATED, panel, comment);
    
    return createOk(queryQuestionCommentTranslator.translate(comment));
  }

  @Override
  @RolesAllowed("user")
  public Response deleteQueryQuestionComment(Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound();
    }

    if (!comment.getCreator().getId().equals(getLoggedUser().getId())) {
      if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_QUERY_COMMENTS)) {
        return createForbidden("Forbidden");
      }
    }
    
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound();
    }
    
    if (inTestMode()) {
      queryQuestionCommentController.deleteQueryQuestionComment(comment);
    } else {
      queryQuestionCommentController.archiveQueryQuestionComment(comment);
    }
    
    publishCommentMqttNotification(QueryQuestionCommentNotification.Type.DELETED, panel, comment);

    return createNoContent();
  }

  @Override
  @RolesAllowed("user")
  public Response findQueryQuestionComment(Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound();
    }
    
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound();
    }
    
    return createOk(queryQuestionCommentTranslator.translate(comment));
  }
  
  @Override
  @RolesAllowed("user")
  public Response listQueryQuestionComments(Long panelId, Long parentId, Long queryId, Long pageId, UUID userId, Long stampId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    Query query = queryId != null ? queryController.findQueryById(queryId) : null;
    if (queryId != null && (query == null || queryController.isQueryArchived(query))) {
      return createBadRequest(String.format("Invalid query id %d", queryId));
    }

    QueryPage queryPage = pageId != null ? queryController.findQueryPageById(pageId) : null;
    if (pageId != null && (queryPage == null || queryController.isQueryPageArchived(queryPage))) {
      return createBadRequest(String.format("Invalid query page id %d", pageId));
    }
    
    PanelStamp stamp = stampId != null ? panelController.findPanelStampById(stampId) : null;
    if (stampId != null && (stamp == null || panelController.isPanelStampArchived(stamp))) {
      return createBadRequest(String.format("Invalid panel stamp id %d", stampId));
    }
    
    User user = userId != null ? userController.findUserByKeycloakId(userId) : null;
    if (userId != null && user == null) {
      return createBadRequest(String.format("Invalid user id %s", userId));
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment parentComment = null; 
    boolean onlyRootComments = false;
    
    if (parentId != null) {
      if (parentId == 0) {
        onlyRootComments = true;
      } else {
        parentComment = queryQuestionCommentController.findQueryQuestionCommentById(parentId);
        if (parentComment == null) {
          return createBadRequest("Invalid parent comment");
        }
      }
    }
    
    return createOk(queryQuestionCommentController.listQueryQuestionComments(panel, stamp, queryPage, query, parentComment, user, onlyRootComments).stream()
      .map(queryQuestionCommentTranslator::translate)
      .collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed("user")  
  public Response updateQueryQuestionComment(QueryQuestionComment body, Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound(String.format("Panel with id %s not found", panelId));
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound(String.format("Comment with id %s not found", commentId));
    }

    if (!comment.getCreator().getId().equals(getLoggedUser().getId())) {
      if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_QUERY_COMMENTS)) {
        return createForbidden("Forbidden");
      }
    }
   
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound("Comment not found from given panel");
    }
 
    String contents = body.getContents();
    Boolean hidden = body.isisHidden();
    Date modified = new Date(System.currentTimeMillis());
    User modifier = getLoggedUser();
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment updatedComment = queryQuestionCommentController.updateQueryQuestionComment(comment, contents, hidden, modifier, modified);

    publishCommentMqttNotification(QueryQuestionCommentNotification.Type.UPDATED, panel, comment);
    
    return createOk(queryQuestionCommentTranslator.translate(updatedComment));
  }

  @Override
  @RolesAllowed("user")  
  public Response findQueryQuestionAnswer(Long panelId, String answerId) {
    QueryQuestionAnswer<?> answerData = queryReplyController.findQueryQuestionAnswerData(answerId);
    if (answerData == null) {
      return createNotFound();
    }
    
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = answerData.getQueryPage();
    if (queryPage == null) {
      return createNotFound();
    }
    
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createNotFound(String.format("Page %d is not from panel %d", queryPage.getId(), panel.getId()));
    }
       
    return createOk(queryQuestionAnswerTranslator.translate(answerData));
  }

  @Override
  @RolesAllowed("user")  
  public Response listQueryQuestionAnswers(Long panelId, Long queryId, Long pageId, UUID userId, Long stampId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    Query query = queryId != null ? queryController.findQueryById(queryId) : null;
    if (queryId != null && (query == null || queryController.isQueryArchived(query))) {
      return createBadRequest(String.format("Invalid query id %d", queryId));
    }

    QueryPage queryPage = pageId != null ? queryController.findQueryPageById(pageId) : null;
    if (queryPage == null || queryController.isQueryPageArchived(queryPage)) {
      return createBadRequest(String.format("Invalid query page id %d", pageId));
    }
    
    PanelStamp stamp = stampId != null ? panelController.findPanelStampById(stampId) : null;
    if (stampId != null && (stamp == null || panelController.isPanelStampArchived(stamp))) {
      return createBadRequest(String.format("Invalid panel stamp id %d", stampId));
    }
    
    User user = userId != null ? userController.findUserByKeycloakId(userId) : null;
    if (userId != null && user == null) {
      return createBadRequest(String.format("Invalid user id %s", userId));
    }
    
    return createOk(queryReplyController.listQueryQuestionAnswers(queryPage, stamp, query, panel.getRootFolder(), user).stream()
      .map(queryQuestionAnswerTranslator::translate)
      .collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed("user")  
  public Response upsertQueryQuestionAnswer(fi.metatavu.edelphi.rest.model.QueryQuestionAnswer body, Long panelId, String answerId) {
    QueryQuestionAnswer<?> answerData = queryReplyController.findQueryQuestionAnswerData(answerId);
    if (answerData == null) {
      return createNotFound();
    }
    
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = answerData.getQueryPage();
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createForbidden("Forbidden");
    }
    
    QueryPageType pageType = queryPage.getPageType();
    
    switch (pageType) {
      case LIVE_2D:
        fi.metatavu.edelphi.rest.model.QueryQuestionLive2dAnswerData data;
        try {
          data = readQueryQuestionAnswerData(body, fi.metatavu.edelphi.rest.model.QueryQuestionLive2dAnswerData.class);
        } catch (IOException e) {
          return createBadRequest("Failed to read data");
        }
        
        QueryQuestionAnswer<QueryQuestionLive2dAnswerData> answer = queryReplyController.setLive2dAnswer(answerData, data.getX(), data.getY());
        publishAnswerMqttNotification(QueryQuestionAnswerNotification.Type.UPDATED, panel, answer);
        
        return createOk(queryQuestionAnswerTranslator.translate(answer));
      default:
        return createInternalServerError(String.format("Pages type %s not supported", pageType));
    }
  }

  @Override
  @RolesAllowed("user")  
  public Response findQueryPage(Long panelId, Long queryPageId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = queryPageController.findQueryPage(queryPageId);
    if (queryPage == null) {
      return createNotFound();
    }
    
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createNotFound(String.format("Page %d is not from panel %d", queryPage.getId(), panel.getId()));
    }
    
    return createOk(queryPageTranslator.translate(queryPage));
  }
  
  /**
   * Reads query question answer data
   * 
   * @param payload payload
   * @param targetClass target class
   * @param <T> return type
   * @return read query question answer data
   * @throws IOException thrown when reading fails
   */
  private <T> T readQueryQuestionAnswerData(fi.metatavu.edelphi.rest.model.QueryQuestionAnswer payload, Class<T> targetClass) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(objectMapper.writeValueAsBytes(payload.getData()), targetClass);
  }

  /**
   * Publishes MQTT notification about comment update 
   * 
   * @param type type
   * @param panel panel
   * @param comment comment
   */
  private void publishCommentMqttNotification(QueryQuestionCommentNotification.Type type, Panel panel, fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment) {
    QueryPage page = comment.getQueryPage();
    Query query = page.getQuerySection().getQuery();
    Long commentParentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
    
    mqttController.publish(QUERY_QUESTION_COMMENTS_MQTT_CHANNEL, new QueryQuestionCommentNotification(type, 
        panel.getId(), 
        query.getId(), 
        page.getId(), 
        comment.getId(),
        commentParentId));
    
  }

  /**
   * Publishes MQTT notification about answer update 
   * 
   * @param type type
   * @param panel panel
   * @param answer answer
   */
  private void publishAnswerMqttNotification(QueryQuestionAnswerNotification.Type type, Panel panel, QueryQuestionAnswer<? extends QueryQuestionAnswerData> answer) {
    QueryPage page = answer.getQueryPage();
    QueryReply queryReply = answer.getQueryReply();
    Query query = page.getQuerySection().getQuery();
    String answerId = String.format("%d-%d", page.getId(), queryReply.getId());
    
    mqttController.publish(QUERY_QUESTION_ANSWERS_MQTT_CHANNEL, new QueryQuestionAnswerNotification(type, 
        panel.getId(), 
        query.getId(), 
        page.getId(), 
        answerId));
    
  }

}