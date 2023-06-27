package it.grid.storm.rest.metadata.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.grid.storm.namespace.model.StoRIType.FILE;
import static it.grid.storm.namespace.model.StoRIType.FOLDER;
import static it.grid.storm.namespace.naming.NamespaceUtil.getWinnerRule;

import java.io.File;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;
import it.grid.storm.namespace.model.VirtualFS;

public class ResourceService {

  private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

  protected Collection<VirtualFS> vfsList;
  protected Collection<MappingRule> rulesList;

  public ResourceService(Collection<VirtualFS> vfsList,
      Collection<MappingRule> rulesList) {

    checkNotNull(vfsList, "Invalid null list of Virtual FS");
    checkNotNull(rulesList, "Invalid null list of Mapping Rules");
    this.vfsList = vfsList;
    this.rulesList = rulesList;
  }

  public StoRI getResource(String stfnPath) throws ResourceNotFoundException, NamespaceException {

    log.debug("Get StoRI resource from {} ...", stfnPath);

    MappingRule rule = getRule(stfnPath);
    log.debug("Mapping rule is {}", rule);

    String relativePath = getRelativePath(rule.getStFNRoot(), stfnPath);
    log.debug("Relative path is {}", relativePath);

    String absolutePath = getAbsolutePath(rule.getMappedFS().getRootPath(), relativePath);
    log.debug("Absolute path is {}", absolutePath);

    StoRIType type = isDirectory(absolutePath) ? FOLDER : FILE;
    log.debug("StoRI type is {}", type);

    return rule.getMappedFS().createFile(relativePath, type, rule);
  }

  private MappingRule getRule(String stfnPath) throws ResourceNotFoundException {

    MappingRule rule = getWinnerRule(stfnPath, rulesList, vfsList);

    if (rule == null) {
      String errorMessage = String.format("Unable to map %s to a rule", stfnPath);
      log.debug(errorMessage);
      throw new ResourceNotFoundException(errorMessage);
    }
    return rule;
  }

  private boolean isDirectory(String absolutePath) {

    return new File(absolutePath).isDirectory();
  }

  private String getRelativePath(String stfnRoot, String stfnPath) {

    String path = stfnPath.replaceFirst(stfnRoot, "");
    if (path.startsWith(File.separator)) {
      path = path.substring(1);
    }
    return path;
  }

  private String getAbsolutePath(String rootPath, String relativePath) {

    if (rootPath.endsWith(File.separator)) {
      return rootPath + relativePath;
    }
    return rootPath + File.separator + relativePath;
  }
}
