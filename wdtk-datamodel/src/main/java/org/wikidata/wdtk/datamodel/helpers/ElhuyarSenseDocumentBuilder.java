package org.wikidata.wdtk.datamodel.helpers;

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;

/**
 * Builder class to construct {@link SenseDocument} objects.
 *
 * @author Elhuyar Foundation
 *
 */
public class ElhuyarSenseDocumentBuilder extends ElhuyarStatementDocumentBuilder<ElhuyarSenseDocumentBuilder, SenseDocument> {
	
	private List<MonolingualTextValue> glosses = new ArrayList<MonolingualTextValue>();
	
	/**
	 * Constructor to start the build from a blank item.
	 *
	 * @param entityIdValue
	 */
	protected ElhuyarSenseDocumentBuilder(EntityIdValue entityIdValue) {
		super(entityIdValue);
	}
	
	/**
	 * Constructor to start the build from an existing sense.
	 * 
	 * @param senseDocument
	 *         the sense to start the build from
	 */
	protected ElhuyarSenseDocumentBuilder(SenseDocument senseDocument) {
		super(senseDocument);
		
	}

	/**
	 * Starts the construction of an {@link SenseDocument} with the given id.
	 *
	 * @param entityIdValue
	 *            id of the newly constructed sense document
	 * @return builder object to continue construction
	 */
	public static ElhuyarSenseDocumentBuilder forSenseId(EntityIdValue entityIdValue) {
		return new ElhuyarSenseDocumentBuilder(entityIdValue);
	}
	
	/**
	 * Starts the construction of an {@link SenseDocument} from an existing value.
	 * 
	 * @param initialDocument
	 * 			  the sense to start the construction from
	 * @return builder object to continue construction
	 */
	public static ElhuyarSenseDocumentBuilder fromSenseDocument(SenseDocument senseDocument) {
		return new ElhuyarSenseDocumentBuilder(senseDocument);
	}

	/**
	 * Returns the {@link SenseDocument} that has been built.
	 *
	 * @return constructed sense document
	 * @throws IllegalStateException
	 *             if the object was built already
	 */
	@Override
	public SenseDocument build() {
		prepareBuild();
		return factory.getSenseDocument((SenseIdValue) this.entityIdValue,
				this.glosses,
				getStatementGroups(),
				this.revisionId);
	}
	
	public ElhuyarSenseDocumentBuilder withGlosses(List<MonolingualTextValue> glosses) {
		this.glosses = glosses;
		return this;
	}
	

	@Override
	protected ElhuyarSenseDocumentBuilder getThis() {
		return this;
	}
}
