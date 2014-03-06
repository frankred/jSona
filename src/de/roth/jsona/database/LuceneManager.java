package de.roth.jsona.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.roth.jsona.config.Config;
import de.roth.jsona.util.Logger;

/**
 * Singleton abstraction class to manage all lucene interactions.
 * 
 * @author Frank Roth
 * 
 */
public class LuceneManager {

	private static final LuceneManager instance = new LuceneManager();

	public static LuceneManager getInstance() {
		return (instance);
	}

	private IndexSearcher searcher;
	private IndexWriter writer;
	private RAMDirectory ramDirectory;
	private IndexWriterConfig config;
	private Analyzer analyzer;

	public IndexSearcher getSearcher() {
		return searcher;
	}

	public final static String SEARCH_FIELD = "all";

	public LuceneManager() {
		try {
			ramDirectory = new RAMDirectory();
			analyzer = new StandardAnalyzer(Version.LUCENE_47);
			config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
			config.setOpenMode(OpenMode.CREATE);

			// lucene writer
			try {
				writer = new IndexWriter(ramDirectory, config);
				writer.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Search in the lucene database with the overgiven search word.
	 * 
	 * @param searchword
	 * @return Results as ScoreDoc[]
	 */
	public ScoreDoc[] search(String searchword) {
		Logger.get().log(Level.ALL, "Search for '" + QueryParser.escape(searchword) + "'.");
		String queryWord = QueryParser.escape(searchword.toLowerCase());
		queryWord = queryWord.replace(" ", "*");
		Query query = new WildcardQuery(new Term(SEARCH_FIELD, "*" + queryWord + "*"));
		try {
			TopDocs docs = searcher.search(query, Config.getInstance().MAX_SEARCH_RESULT_AMOUNT);
			return docs.scoreDocs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Add a lucene document to the database.
	 * 
	 * @param doc
	 */
	public void add(Document doc) {
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commit changes, this method has to be called if you added or changed
	 * something in the lucene database. After committing all modifications a
	 * new searcher will be created.
	 */
	public void commit() {
		try {
			writer.commit();
			searcher = new IndexSearcher(DirectoryReader.open(ramDirectory));
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete a document by uuid.
	 * 
	 * @param id
	 */
	public void delete(String id) {
		try {
			writer.deleteDocuments(new Term("id", id));
			writer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the amount of documents in the database.
	 * 
	 * @return Amount of documents
	 */
	public int getAmount() {
		return searcher.getIndexReader().numDocs();
	}

	/**
	 * Returns all documents in the lucene database.
	 * 
	 * @return
	 */
	public ArrayList<Document> getAllDocuments() {
		IndexReader reader = searcher.getIndexReader();

		ArrayList<Document> docs = new ArrayList<Document>();

		for (int i = 0; i < reader.maxDoc(); i++) {
			try {
				docs.add(reader.document(i));
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return docs;
	}
}