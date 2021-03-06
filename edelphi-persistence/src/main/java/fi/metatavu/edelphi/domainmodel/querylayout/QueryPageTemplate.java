package fi.metatavu.edelphi.domainmodel.querylayout;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.metatavu.edelphi.domainmodel.base.ArchivableEntity;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.metatavu.edelphi.domainmodel.users.User;

@Entity
public class QueryPageTemplate implements ArchivableEntity, ModificationTrackedEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "QueryPageTemplate")
  @TableGenerator(name = "QueryPageTemplate", allocationSize = 1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne 
  private LocalizedEntry name;
  
  @ManyToOne 
  private LocalizedEntry description;
  
  @NotNull
  @Column(nullable = false)
  private String iconName;

  @NotNull
  @Column(nullable = false)
  @Enumerated (EnumType.STRING)
  private QueryPageType pageType;
  
  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
  
  @ManyToOne 
  private User creator;
  
  @NotNull
  @Column (updatable=false, nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date created;
  
  @ManyToOne  
  private User lastModifier;
  
  @NotNull
  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date lastModified;
  
  public Long getId() {
    return id;
  }
  
  public LocalizedEntry getName() {
    return name;
  }
  
  public void setName(LocalizedEntry name) {
    this.name = name;
  }
  
  public String getIconName() {
    return iconName;
  }
  
  public void setIconName(String iconName) {
    this.iconName = iconName;
  }
  
  public QueryPageType getPageType() {
    return pageType;
  }
  
  public void setPageType(QueryPageType pageType) {
    this.pageType = pageType;
  }
  
  @Override
  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public Date getCreated() {
    return created;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Override
  public Boolean getArchived() {
    return archived;
  }

  public LocalizedEntry getDescription() {
    return description;
  }

  public void setDescription(LocalizedEntry description) {
    this.description = description;
  }
}
