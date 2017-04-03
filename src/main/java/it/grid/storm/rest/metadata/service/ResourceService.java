package it.grid.storm.rest.metadata.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.grid.storm.namespace.naming.NamespaceUtil.getWinnerRule;

import java.io.File;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;

public class ResourceService {

	private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

	private Collection<VirtualFSInterface> vfsList;
	private Collection<MappingRule> rulesList;

	public ResourceService(Collection<VirtualFSInterface> vfsList,
			Collection<MappingRule> rulesList) {

		checkNotNull(vfsList, "Invalid null list of Virtual FS");
		checkNotNull(rulesList, "Invalid null list of Mapping Rules");
		this.vfsList = vfsList;
		this.rulesList = rulesList;
	}

	public StoRI getResource(String stfnPath) throws ResourceNotFoundException, NamespaceException {

		MappingRule rule = getWinnerRule(stfnPath, rulesList, vfsList);

		if (rule == null) {
			String errorMessage = String.format("Unable to map %s to a rule", stfnPath);
			log.debug(errorMessage);
			throw new ResourceNotFoundException(errorMessage);
		}

		String fileRelativePath = stfnPath.replaceFirst(rule.getStFNRoot() + File.separator, "");
		log.debug("File relative path is {}", fileRelativePath);

		return rule.getMappedFS().createFile(fileRelativePath, StoRIType.FILE, rule);
	}

}
