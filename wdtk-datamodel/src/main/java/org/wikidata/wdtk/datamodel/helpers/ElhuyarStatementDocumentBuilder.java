package org.wikidata.wdtk.datamodel.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

/**
 * Abstract base class for builders that construct {@link StatementDocument}
 * objects.
 *
 * @author Elhuyar Foundation
 *
 * @param <T> the type of the eventual concrete builder implementation
 * @param <O> the type of the object that is being built
 */
public abstract class ElhuyarStatementDocumentBuilder<T extends ElhuyarStatementDocumentBuilder<T, O>, O extends StatementDocument>
		extends AbstractDataObjectBuilder<T, O> {

	final EntityIdValue entityIdValue;

	final HashMap<PropertyIdValue, ArrayList<Statement>> statements = new HashMap<>();

	long revisionId = 0;

	protected ElhuyarStatementDocumentBuilder(EntityIdValue entityIdValue) {
		this.entityIdValue = entityIdValue;
	}

	/**
	 * Starts constructing an StatementDocument from an initial version of this
	 * document.
	 * 
	 * @param initialDocument the initial version of the document to use
	 */
	protected ElhuyarStatementDocumentBuilder(O initialDocument) {
		this.entityIdValue = initialDocument.getEntityId();
		this.revisionId = initialDocument.getRevisionId();

		Iterator<Statement> iterator = initialDocument.getAllStatements();
		while (iterator.hasNext()) {
			withStatement(iterator.next());
		}
	}

	/**
	 * Sets the revision id for the constructed document. See
	 * {@link EntityDocument#getRevisionId()}.
	 *
	 * @param revisionId the revision id
	 * @return builder object to continue construction
	 */
	public T withRevisionId(long revisionId) {
		this.revisionId = revisionId;
		return getThis();
	}

	/**
	 * Adds an additional statement to the constructed document.
	 *
	 * @param statement the additional statement
	 * @return builder object to continue construction
	 */
	public T withStatement(Statement statement) {
		PropertyIdValue pid = statement.getMainSnak().getPropertyId();
		ArrayList<Statement> pidStatements = this.statements.get(pid);
		if (pidStatements == null) {
			pidStatements = new ArrayList<Statement>();
			this.statements.put(pid, pidStatements);
		}

		pidStatements.add(statement);
		return getThis();
	}

	/**
	 * Returns a list of {@link StatementGroup} objects for the currently stored
	 * statements.
	 *
	 * @return
	 */
	protected List<StatementGroup> getStatementGroups() {
		ArrayList<StatementGroup> result = new ArrayList<>(this.statements.size());
		for (ArrayList<Statement> statementList : this.statements.values()) {
			result.add(factory.getStatementGroup(statementList));
		}
		return result;
	}

}
