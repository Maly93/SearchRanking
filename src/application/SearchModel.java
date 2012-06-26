package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

public class SearchModel {

	private String apiKey;										// The Key for using the Google Custom Search API
	private String searchKey;									// The Key to the Custom Search Engine
	private String search;										// The String being searched for
	private int resultIndex;									// The index of the desired result.
	private Customsearch customsearch;							// A Customsearch object used for interacting with the search engine
	private List<Result> items;									// The Results List from the Search
	private final String TARGET_URL = "washingtonpost.com";		// The Target URL containing "washingtonpost.com"
	private Result target;										// The Desired Result containing the Targeted URL
	private Result topResult;									// The Top Result on the list.
	
	/**
	 * Creates a new SearchModel object.
	 */
	// TODO Change to the newer version of creating the Customsearch object.
	@SuppressWarnings("deprecation")
	public SearchModel()
	{
		customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory());
		items = new ArrayList<Result>();
		target = new Result();
		topResult = null;
	}

	/**
	 * Creates a new SearchModel object with initialized api and search key fields.
	 * @param apiKey the API key.
	 * @param searchKey the Search Engine Key
	 */
	public SearchModel(String apiKey, String searchKey)
	{
		this();

		setApiKey(apiKey);
		setSearchKey(searchKey);
	}

	//----------------------------------------------------------------------
	// Getter and Setter Methods
	//----------------------------------------------------------------------

	/**
	 * Gets the API key for the SearchModel.
	 * @return the API key.
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * Gets the Custom Engine Search key for the SearchModel.
	 * @return the search engine key.
	 */
	public String getSearchKey() {
		return searchKey;
	}

	/**
	 * Gets the current Search String.
	 * @return the current Search String
	 */
	public String getSearchString() {
		return search;
	}

	/**
	 * Gets the Customsearch being used by the SearchModel.
	 * @return the Customsearch.
	 */
	public Customsearch getCustomsearch(){
		return customsearch;
	}

	/**
	 * Gets the target url being searched for within the results.
	 * @return the target url.
	 */
	public String getTargetUrl(){
		return TARGET_URL;
	}

	/**
	 * Gets the List search results.
	 * @return the results
	 */
	public List<Result> getResults(){
		return items;
	}

	/**
	 * Returns the index of the desired result.
	 * @return the result index.
	 */
	public int getResultIndex(){
		return resultIndex;
	}


	/**
	 * Sets a new API key for the SearchModel.
	 * @param apiKey the key.
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Sets a new Custom Search Engine key for the SearchModel.
	 * @param searchKey the key.
	 */
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	/**
	 * Sets a new String.
	 * @param search the Search String.
	 */
	public void setSearch(String search) {
		this.search = search;
	}

	/**
	 * Gets the target Result.
	 * @return the target result.
	 */
	public Result getTargetResult(){
		return target;
	}

	/**
	 * Gets the top Result of a search.
	 * @return the top result or null if no search has been completed.
	 */
	public Result getTopResult(){
		return topResult;
	}

	//----------------------------------------------------------------
	// Application Methods
	//----------------------------------------------------------------

	/**
	 * Wrapper method for searching a single page.
	 * @param str The String being searched for.
	 */
	public void search(String str)
	{
		assert str != null : "Cannot search for a null value";
		
		setSearch(str);
		search();
		findTarget();

	}

	/**
	 * Searches for the specified String through the number of pages (up to 10)
	 * @param str
	 * @param numPages
	 */
	public void search(String str, int numPages)
	{
		assert str != null : "Cannot search for a null value";
		assert numPages >= 0 : "Cannot search for a negative value";
		assert numPages <= 10 : "Cannot search more than ten pages";
		
		setSearch(str);
		
		for (int i = 0; i < numPages; i++)
		{
			search(i);
		}
			
		findTarget();
	}

	/**
	 * Searches for a Strings contained in a list.
	 * @param searches the List of Strings.
	 */
	public void search(List<String> searches)
	{
		for (String str : searches)
		{
			search(str);
		}
	}
	
	/**
	 * Performs a search for the specified String.
	 */
	private void search()
	{
		search(0);
	}

	/**
	 * Performs a search starting at the specified page number.
	 * @param startPage the result page to be started at (0 - 9)
	 */
	private void search(int startPage)
	{
		com.google.api.services.customsearch.Customsearch.Cse.List list = null;
		try {
			
			list = customsearch.cse().list(search);
			
		} catch (IOException e1) {
			
			throw new RuntimeException("Unable to complete search, check configuration.");
		}
		
		if (list == null)
		{
			return;
		}
		
		list.setStart((long) startPage * 10);
		list.setKey(apiKey);
		list.setCx(searchKey);
		
		Search results = null;
		
		try
		{
			results = list.execute();
		} catch(IOException e)
		{
			throw new RuntimeException("Unable to retrieve results, check API and Search Engine Keys.");
		}
		
		if (results == null)
		{
			return;
		}
		
		items = results.getItems();
	}
	
	/**
	 * Determines if the target index is found.
	 * @return true if the target is found, false if not.
	 */
	public boolean findTarget()
	{
		boolean found = false;
		
		for (int i = 0; i < items.size(); i++)
		{
			if ( !found && items.get(i).getFormattedUrl().contains(TARGET_URL))
			{
				resultIndex = i;
				found = true;
			}
		}

		if (found)
		{
			target = items.get(resultIndex);
			topResult = items.get(0);
		}
		else
		{
			topResult = items.get(0);
			target.setTitle(search);
			target.setSnippet("Not Found");
			target.setFormattedUrl("Not Found");
		}
		
		return found;
	}

	//	/**
	//	 * Cycles through the Result set printing the results
	//	 * @precondition A search has been completed first.
	//	 */
	//	public void printResults()
	//	{
	//		assert items.size() > 0 : "A search must be completed first.";
	//
	//		for (int i = 0; i < items.size(); i++)
	//		{
	//			System.out.println("Title " + i + ": " + items.get(i).getTitle());
	//			System.out.println("Snippet: " + items.get(i).getSnippet());
	//			System.out.println("URL: " + items.get(i).getFormattedUrl());
	//		}
	//	}

	/**
	 * Returns a string representation of the List of results.
	 * @precondition A search has been completed before the method can be called.
	 * @return the Result list.
	 */
	public String getResultString()
	{
		assert items.size() > 0 : "A Search must be completed first";

		StringBuilder build = new StringBuilder();

		for (int i = 0; i < items.size(); i++)
		{
			build.append("Title " + i + ": " + items.get(i).getTitle() + "\n");
			build.append("URL: " + items.get(i).getFormattedUrl() + "\n");
		}

		return build.toString();
	}

	/**
	 * Clears the saved list of results.
	 */
	public void clearResults()
	{
		items.clear();
	}
	
	
}