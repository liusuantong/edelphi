package fi.metatavu.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.query.AbstractQueryPageHandler;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.comments.GenericReportPageCommentProcessor;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;

public abstract class AbstractThesisQueryPageHandler extends AbstractQueryPageHandler {
 
  private List<QueryOption> options = new ArrayList<>();

  public AbstractThesisQueryPageHandler() {
  	super();

    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.text", "panelAdmin.block.query.thesisTextOptionLabel", QueryOptionEditor.MEMO, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.description", "panelAdmin.block.query.thesisDescriptionOptionLabel", QueryOptionEditor.MEMO, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.commentable", "panelAdmin.block.query.thesisCommentableOptionLabel", QueryOptionEditor.BOOLEAN, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.viewDiscussions", "panelAdmin.block.query.thesisViewDiscussionsOptionLabel", QueryOptionEditor.BOOLEAN, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.showLiveReport", "panelAdmin.block.query.thesisShowLiveReportOptionLabel", QueryOptionEditor.BOOLEAN, true));
  }
  
  @Override
  public void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    requestContext.getRequest().setAttribute("queryPageId", queryPage.getId());
    
    renderThesis(requestContext, queryPage);
    renderDescription(requestContext, queryPage);
    renderQuestion(requestContext, queryPage, queryReply);

    QuerySection section = queryPage.getQuerySection();
    
    if (getBooleanOptionValue(queryPage, getDefinedOption("thesis.showLiveReport")))
      renderReport(requestContext, queryPage);
    
    boolean commentable = (section.getCommentable() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("thesis.commentable"));
    boolean viewDiscussions = (section.getViewDiscussions() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("thesis.viewDiscussions"));
    renderComments(requestContext, queryPage, queryReply, commentable, viewDiscussions);
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    Boolean thesisCommentable = "1".equals(settings.get("thesis.commentable"));
    Boolean thesisViewDiscussions = "1".equals(settings.get("thesis.viewDiscussions"));
    Boolean thesisShowLiveReport = "1".equals(settings.get("thesis.showLiveReport"));
    
    // commentable, viewDiscussions, showLiveReport, text and description settings 
    // can all be updated regardless whether query has any answers 
    
    QueryPageUtils.setSetting(queryPage, "thesis.commentable", thesisCommentable ? "1" : "0", modifier);
    QueryPageUtils.setSetting(queryPage, "thesis.viewDiscussions", thesisViewDiscussions ? "1" : "0", modifier);
    QueryPageUtils.setSetting(queryPage, "thesis.showLiveReport", thesisShowLiveReport ? "1" : "0", modifier);
    
    String thesisText = settings.get("thesis.text");
    String thesisDescription = settings.get("thesis.description");

    QueryPageUtils.setSetting(queryPage, "thesis.text", thesisText, modifier);
    QueryPageUtils.setSetting(queryPage, "thesis.description", thesisDescription, modifier);
  }
  
  @Override
  public void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    saveThesisAnswers(requestContext, queryPage, queryReply);

    Messages messages = Messages.getInstance();
    Locale locale = requestContext.getRequest().getLocale();
    QuerySection section = queryPage.getQuerySection();

    // Save comment
    if (section.getCommentable() == Boolean.TRUE && getBooleanOptionValue(queryPage, getDefinedOption("thesis.commentable"))) {
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

  protected void renderThesis(PageRequestContext requestContext, QueryPage queryPage) {
    RequiredQueryFragment thesisFragment = new RequiredQueryFragment("thesis");
    thesisFragment.addAttribute("text", getStringOptionValue(queryPage, getDefinedOption("thesis.text")));
    addRequiredFragment(requestContext, thesisFragment);
  }

  protected void renderDescription(PageRequestContext requestContext, QueryPage queryPage) {
    RequiredQueryFragment descriptionFragment = new RequiredQueryFragment("description");
    descriptionFragment.addAttribute("text", getStringOptionValue(queryPage, getDefinedOption("thesis.description")));
    addRequiredFragment(requestContext, descriptionFragment);
  }

  protected abstract void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply);
  protected abstract void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply);

  protected abstract void renderReport(PageRequestContext requestContext, QueryPage queryPage);
  
}
