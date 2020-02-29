
package fi.metatavu.edelphi.queries.batch;

import java.util.Locale;

import javax.batch.api.listener.AbstractJobListener;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.i18n.BatchMessages;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.mail.Mailer;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.resources.ResourceController;

/**
 * Batch listener for notifying report recipients about failed query copy failures
 * 
 * @author Antti Leppä
 */
@Named
public class QueryCopyStatusListener extends AbstractJobListener {
  
  @Inject
  private Logger logger;

  @Inject
  private Mailer mailer;
  
  @Inject
  private BatchMessages batchMessages;

  @Inject
  private QueryController queryController;

  @Inject
  private ResourceController resourceController;
  
  @Inject
  private JobContext jobContext;

  @Inject
  @JobProperty
  private String deliveryEmail;
  
  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long queryId;

  @Override
  @Transactional (value = TxType.REQUIRES_NEW)
  public void afterJob() throws Exception {
    switch (jobContext.getBatchStatus()) {
      case FAILED:
        sendFailureNotificationEmail();
      break;
      default:
      break;
    }
  }

  /**
   * Sends an email about report generation failure
   */
  private void sendFailureNotificationEmail() {
    if (StringUtils.isBlank(deliveryEmail) || locale == null) {
      logger.warn("Failed to send copy error message because email was blank");
      return;
    }
    
    Query query = queryId != null ? queryController.findQueryById(queryId) : null;
    if (query == null) {
      logger.warn("Failed to send report generation error message because query was null");
      return;
    }
    
    Panel panel = query != null ? resourceController.getResourcePanel(query) : null;
    if (panel == null) {
      logger.warn("Failed to send report generation error message because panel was null");
      return;
    }
    
    String subject = batchMessages.getText(locale, "batch.copyQuery.error.mail.subject");
    String content = batchMessages.getText(locale, "batch.copyQuery.error.mail.content", panel.getId(), query.getId());
    
    Email email = EmailBuilder.startingBlank()
      .from("noreply@edelphi.org")
      .to(deliveryEmail)
      .withSubject(subject)
      .withHTMLText(content)
      .buildEmail();
    
    mailer.sendMail(email);
  }
  
}
