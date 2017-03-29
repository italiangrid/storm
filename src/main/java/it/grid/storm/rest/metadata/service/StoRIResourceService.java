package it.grid.storm.rest.metadata.service;

import static it.grid.storm.namespace.naming.NamespaceUtil.getWinnerRule;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.io.File;
import java.util.Collection;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.StoRIType;

public class StoRIResourceService {

	private static final Logger log = LoggerFactory.getLogger(StoRIResourceService.class);

	private Collection<VirtualFSInterface> vfsList;
	private Collection<MappingRule> rulesList;

	public StoRIResourceService(Collection<VirtualFSInterface> vfsList,
			Collection<MappingRule> rulesList) {

		this.vfsList = vfsList;
		this.rulesList = rulesList;
	}

	public StoRI getResource(String stfnPath) throws WebApplicationException {

		MappingRule rule = getMappingRule(stfnPath);

		String fileRelativePath = stfnPath.replaceFirst(rule.getStFNRoot() + File.separator, "");
		log.debug("File relative path is {}", fileRelativePath);

		try {

			return rule.getMappedFS().createFile(fileRelativePath, StoRIType.FILE, rule);

		} catch (NamespaceException e) {

			e.printStackTrace();
			throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);

		}
	}

	private MappingRule getMappingRule(String stfnPath) throws WebApplicationException {
		MappingRule rule = null;
		try {
			rule = getWinnerRule(stfnPath, rulesList, vfsList);
		} catch (Throwable e) {
			throw new WebApplicationException(e.getMessage(), INTERNAL_SERVER_ERROR);
		}
		if (rule == null) {
			String errorMessage = String.format("Unable to map %s to a rule", stfnPath);
			log.debug(errorMessage);
			throw new WebApplicationException(errorMessage, BAD_REQUEST);
		}
		return rule;
	}

}
