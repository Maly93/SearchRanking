package application;

import junit.framework.TestCase;

public class SearchModelTest extends TestCase {

	private SearchModel testSearch;

	/**
	 * Called before each test case. Initializes test fixtures. 
	 */
	public void setUp()
	{
		testSearch = new SearchModel();
	}

	/**
	 * Tests the basic getters and setters for SearchModel, as well as the constructor. 
	 */
	public void testBasics()
	{
		assertNotNull(testSearch.getCustomsearch());
		assertEquals("washingtonpost.com", testSearch.getTargetUrl());
		assertNotNull(testSearch.getResults());
		assertEquals(0, testSearch.getResults().size());
		assertEquals(0, testSearch.getResultIndex());

		testSearch.setApiKey("AIzaSyB7vuvdjKtt4AGHPjCPXPsnL5CoGOGo3Y8");
		testSearch.setSearchKey("012661291032744890452:ikdi3gyy1rk");
		testSearch.setSearch("DC News");

		assertEquals("AIzaSyB7vuvdjKtt4AGHPjCPXPsnL5CoGOGo3Y8", testSearch.getApiKey());
		assertEquals("012661291032744890452:ikdi3gyy1rk", testSearch.getSearchKey());
		assertEquals("DC News", testSearch.getSearchString());

	}

	public void testSearch()
	{
		testSearch.setApiKey("AIzaSyB_HVYobrNwA_h8pDSxlfZj-2iH4IfxfKc");
		testSearch.setSearchKey("012661291032744890452:ikdi3gyy1rk");


		testSearch.search("DC News");

		assertTrue(testSearch.getResults().size() > 0);
		assertEquals(testSearch.getResults().size(), 10);
		assertTrue(testSearch.findTarget());
	}

}
