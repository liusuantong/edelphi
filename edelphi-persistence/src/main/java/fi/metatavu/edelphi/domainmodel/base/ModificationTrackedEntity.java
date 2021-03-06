package fi.metatavu.edelphi.domainmodel.base;

import java.util.Date;

import fi.metatavu.edelphi.domainmodel.users.User;

public interface ModificationTrackedEntity {

  void setCreator(User creator);

  User getCreator();

  void setCreated(Date created);

  Date getCreated();

  void setLastModifier(User lastModifier);

  User getLastModifier();

  void setLastModified(Date lastModified);

  Date getLastModified();
}
