package fi.metatavu.edelphi.query.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.query.AbstractQueryPageHandler;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.comments.GenericReportPageCommentProcessor;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;

public class TextQueryPageHandler extends AbstractQueryPageHandler {

  public TextQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.TEXT, "text.content", "panelAdmin.block.query.textContentOptionLabel", QueryOptionEditor.MEMO, true));
    options.add(new QueryOption(QueryOptionType.TEXT, "text.commentable", "panelAdmin.block.query.textCommentableOptionLabel", QueryOptionEditor.BOOLEAN, true));
    options.add(new QueryOption(QueryOptionType.TEXT, "text.viewDiscussions", "panelAdmin.block.query.textViewDiscussionsOptionLabel", QueryOptionEditor.BOOLEAN, true));
  }
  
  @Override
  public void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    requestContext.getRequest().setAttribute("queryPageId", queryPage.getId());

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("text");
    
    requiredFragment.addAttribute("text", getStringOptionValue(queryPage, getDefinedOption("text.content")));
    addRequiredFragment(requestContext, requiredFragment);

    QuerySection section = queryPage.getQuerySection();
    boolean commentable = (section.getCommentable() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("text.commentable"));
    boolean viewDiscussion = (section.getViewDiscussions() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("text.viewDiscussions"));
    
    renderComments(requestContext, queryPage, queryReply, commentable, viewDiscussion);
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.TEXT)
        QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
    }
  }

  @Override
  public void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QuerySection section = queryPage.getQuerySection();

    Messages messages = Messages.getInstance();
    Locale locale = requestContext.getRequest().getLocale();
    
    // Save comment
    if (section.getCommentable() == Boolean.TRUE && getBooleanOptionValue(queryPage, getDefinedOption("text.commentable"))) {
      QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();

      User loggedUser = RequestUtils.getUser(requestContext);
      
      // Root level comment
      String commentText = requestContext.getString("comment");

      if (!StringUtils.isEmpty(commentText)) {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        
        if (comment != null) {
          if (!commentText.equals(comment.getComment())) {
            queryQuestionCommentDAO.updateComment(comment, commentText, loggedUser);
          }
        }
        else {
          queryQuestionCommentDAO.create(queryReply, queryPage, null, commentText, false, loggedUser);
        }
      }
    }
  }
  
  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return new GenericReportPageCommentProcessor(stamp, queryPage, new HashMap<>());
  }
  
  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }
  
  @Override
  public void exportData(QueryExportContext exportContext) {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    QueryPage queryPage = exportContext.getQueryPage();
    boolean commentable = Boolean.TRUE.equals(this.getBooleanOptionValue(queryPage,  getDefinedOption("text.commentable")));
    if (commentable) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle());
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        exportContext.addCellValue(queryReply, columnIndex, comment != null ? comment.getComment() : null);
      }
    }
  }

  protected void renderCommentEditor(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    RequiredQueryFragment commentEditorFragment = new RequiredQueryFragment("comment_editor");

    if (queryReply != null) {
      QueryQuestionCommentDAO commentDAO = new QueryQuestionCommentDAO();
      QueryQuestionComment comment = commentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
  
      if (comment != null) {
        commentEditorFragment.addAttribute("userCommentId", comment.getId().toString());
        commentEditorFragment.addAttribute("userCommentContent", comment.getComment());
      }
    }
    
    addRequiredFragment(requestContext, commentEditorFragment);
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
}
