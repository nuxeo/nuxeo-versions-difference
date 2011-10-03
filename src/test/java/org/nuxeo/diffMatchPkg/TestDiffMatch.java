/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Davide Taibi & Antonio Urso
 *     Università dell'Insubria
 */


package org.nuxeo.diffMatchPkg;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.nuxeo.diffMatchPkg.DiffMatch.Diff;
import org.nuxeo.diffMatchPkg.DiffMatch.LinesToCharsResult;

public class TestDiffMatch extends TestCase {

  private DiffMatch dmp;
  private DiffMatch.Operation DELETE = DiffMatch.Operation.DELETE;
  private DiffMatch.Operation EQUAL = DiffMatch.Operation.EQUAL;
  private DiffMatch.Operation INSERT = DiffMatch.Operation.INSERT;

  protected void setUp() {
    // Create an instance of the diff_match_patch object.
    dmp = new DiffMatch();
  }


  //  DIFF TEST FUNCTIONS


  public void testDiffCommonPrefix() {
    // Detect any common prefix.
    assertEquals("diff_commonPrefix: Null case.", 0, dmp.diff_commonPrefix("abc", "xyz"));

    assertEquals("diff_commonPrefix: Non-null case.", 4, dmp.diff_commonPrefix("1234abcdef", "1234xyz"));

    assertEquals("diff_commonPrefix: Whole case.", 4, dmp.diff_commonPrefix("1234", "1234xyz"));
  }

  public void testDiffCommonSuffix() {
    // Detect any common suffix.
    assertEquals("diff_commonSuffix: Null case.", 0, dmp.diff_commonSuffix("abc", "xyz"));

    assertEquals("diff_commonSuffix: Non-null case.", 4, dmp.diff_commonSuffix("abcdef1234", "xyz1234"));

    assertEquals("diff_commonSuffix: Whole case.", 4, dmp.diff_commonSuffix("1234", "xyz1234"));
  }

  public void testDiffCommonOverlap() {
    // Detect any suffix/prefix overlap.
    assertEquals("diff_commonOverlap: Null case.", 0, dmp.diff_commonOverlap("", "abcd"));

    assertEquals("diff_commonOverlap: Whole case.", 3, dmp.diff_commonOverlap("abc", "abcd"));

    assertEquals("diff_commonOverlap: No overlap.", 0, dmp.diff_commonOverlap("123456", "abcd"));

    assertEquals("diff_commonOverlap: Overlap.", 3, dmp.diff_commonOverlap("123456xxx", "xxxabcd"));
  }

  public void testDiffHalfmatch() {
    // Detect a halfmatch.
    dmp.Diff_Timeout = 1;
    assertNull("diff_halfMatch: No match #1.", dmp.diff_halfMatch("1234567890", "abcdef"));

    assertNull("diff_halfMatch: No match #2.", dmp.diff_halfMatch("12345", "23"));

    assertArrayEquals("diff_halfMatch: Single Match #1.", new String[]{"12", "90", "a", "z", "345678"}, dmp.diff_halfMatch("1234567890", "a345678z"));

    assertArrayEquals("diff_halfMatch: Single Match #2.", new String[]{"a", "z", "12", "90", "345678"}, dmp.diff_halfMatch("a345678z", "1234567890"));

    assertArrayEquals("diff_halfMatch: Single Match #3.", new String[]{"abc", "z", "1234", "0", "56789"}, dmp.diff_halfMatch("abc56789z", "1234567890"));

    assertArrayEquals("diff_halfMatch: Single Match #4.", new String[]{"a", "xyz", "1", "7890", "23456"}, dmp.diff_halfMatch("a23456xyz", "1234567890"));

    assertArrayEquals("diff_halfMatch: Multiple Matches #1.", new String[]{"12123", "123121", "a", "z", "1234123451234"}, dmp.diff_halfMatch("121231234123451234123121", "a1234123451234z"));

    assertArrayEquals("diff_halfMatch: Multiple Matches #2.", new String[]{"", "-=-=-=-=-=", "x", "", "x-=-=-=-=-=-=-="}, dmp.diff_halfMatch("x-=-=-=-=-=-=-=-=-=-=-=-=", "xx-=-=-=-=-=-=-="));

    assertArrayEquals("diff_halfMatch: Multiple Matches #3.", new String[]{"-=-=-=-=-=", "", "", "y", "-=-=-=-=-=-=-=y"}, dmp.diff_halfMatch("-=-=-=-=-=-=-=-=-=-=-=-=y", "-=-=-=-=-=-=-=yy"));

    // Optimal diff would be -q+x=H-i+e=lloHe+Hu=llo-Hew+y not -qHillo+x=HelloHe-w+Hulloy
    assertArrayEquals("diff_halfMatch: Non-optimal halfmatch.", new String[]{"qHillo", "w", "x", "Hulloy", "HelloHe"}, dmp.diff_halfMatch("qHilloHelloHew", "xHelloHeHulloy"));

    dmp.Diff_Timeout = 0;
    assertNull("diff_halfMatch: Optimal no halfmatch.", dmp.diff_halfMatch("qHilloHelloHew", "xHelloHeHulloy"));
  }

  public void testDiffLinesToChars() {
    // Convert lines down to characters.
    ArrayList<String> tmpVector = new ArrayList<String>();
    tmpVector.add("");
    tmpVector.add("alpha\n");
    tmpVector.add("beta\n");
    assertLinesToCharsResultEquals("diff_linesToChars:", new LinesToCharsResult("\u0001\u0002\u0001", "\u0002\u0001\u0002", tmpVector), dmp.diff_linesToChars("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n"));

    tmpVector.clear();
    tmpVector.add("");
    tmpVector.add("alpha\r\n");
    tmpVector.add("beta\r\n");
    tmpVector.add("\r\n");
    assertLinesToCharsResultEquals("diff_linesToChars:", new LinesToCharsResult("", "\u0001\u0002\u0003\u0003", tmpVector), dmp.diff_linesToChars("", "alpha\r\nbeta\r\n\r\n\r\n"));

    tmpVector.clear();
    tmpVector.add("");
    tmpVector.add("a");
    tmpVector.add("b");
    assertLinesToCharsResultEquals("diff_linesToChars:", new LinesToCharsResult("\u0001", "\u0002", tmpVector), dmp.diff_linesToChars("a", "b"));

    // More than 256 to reveal any 8-bit limitations.
    int n = 300;
    tmpVector.clear();
    StringBuilder lineList = new StringBuilder();
    StringBuilder charList = new StringBuilder();
    for (int x = 1; x < n + 1; x++) {
      tmpVector.add(x + "\n");
      lineList.append(x + "\n");
      charList.append(String.valueOf((char) x));
    }
    assertEquals(n, tmpVector.size());
    String lines = lineList.toString();
    String chars = charList.toString();
    assertEquals(n, chars.length());
    tmpVector.add(0, "");
    assertLinesToCharsResultEquals("diff_linesToChars: More than 256.", new LinesToCharsResult(chars, "", tmpVector), dmp.diff_linesToChars(lines, ""));
  }

  public void testDiffCharsToLines() {
    // First check that Diff equality works.
    assertTrue("diff_charsToLines:", new Diff(EQUAL, "a").equals(new Diff(EQUAL, "a")));

    assertEquals("diff_charsToLines:", new Diff(EQUAL, "a"), new Diff(EQUAL, "a"));

    // Convert chars up to lines
    LinkedList<Diff> diffs = diffList(new Diff(EQUAL, "\u0001\u0002\u0001"), new Diff(INSERT, "\u0002\u0001\u0002"));
    ArrayList<String> tmpVector = new ArrayList<String>();
    tmpVector.add("");
    tmpVector.add("alpha\n");
    tmpVector.add("beta\n");
    dmp.diff_charsToLines(diffs, tmpVector);
    assertEquals("diff_charsToLines:", diffList(new Diff(EQUAL, "alpha\nbeta\nalpha\n"), new Diff(INSERT, "beta\nalpha\nbeta\n")), diffs);

    // More than 256 to reveal any 8-bit limitations.
    int n = 300;
    tmpVector.clear();
    StringBuilder lineList = new StringBuilder();
    StringBuilder charList = new StringBuilder();
    for (int x = 1; x < n + 1; x++) {
      tmpVector.add(x + "\n");
      lineList.append(x + "\n");
      charList.append(String.valueOf((char) x));
    }
    assertEquals(n, tmpVector.size());
    String lines = lineList.toString();
    String chars = charList.toString();
    assertEquals(n, chars.length());
    tmpVector.add(0, "");
    diffs = diffList(new Diff(DELETE, chars));
    dmp.diff_charsToLines(diffs, tmpVector);
    assertEquals("diff_charsToLines: More than 256.", diffList(new Diff(DELETE, lines)), diffs);
  }

  public void testDiffCleanupMerge() {
    // Cleanup a messy diff.
    LinkedList<Diff> diffs = diffList();
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Null case.", diffList(), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(INSERT, "c"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: No change case.", diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(INSERT, "c")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(EQUAL, "b"), new Diff(EQUAL, "c"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Merge equalities.", diffList(new Diff(EQUAL, "abc")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(DELETE, "b"), new Diff(DELETE, "c"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Merge deletions.", diffList(new Diff(DELETE, "abc")), diffs);

    diffs = diffList(new Diff(INSERT, "a"), new Diff(INSERT, "b"), new Diff(INSERT, "c"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Merge insertions.", diffList(new Diff(INSERT, "abc")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "b"), new Diff(DELETE, "c"), new Diff(INSERT, "d"), new Diff(EQUAL, "e"), new Diff(EQUAL, "f"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Merge interweave.", diffList(new Diff(DELETE, "ac"), new Diff(INSERT, "bd"), new Diff(EQUAL, "ef")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "abc"), new Diff(DELETE, "dc"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Prefix and suffix detection.", diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "d"), new Diff(INSERT, "b"), new Diff(EQUAL, "c")), diffs);

    diffs = diffList(new Diff(EQUAL, "x"), new Diff(DELETE, "a"), new Diff(INSERT, "abc"), new Diff(DELETE, "dc"), new Diff(EQUAL, "y"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Prefix and suffix detection with equalities.", diffList(new Diff(EQUAL, "xa"), new Diff(DELETE, "d"), new Diff(INSERT, "b"), new Diff(EQUAL, "cy")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(INSERT, "ba"), new Diff(EQUAL, "c"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Slide edit left.", diffList(new Diff(INSERT, "ab"), new Diff(EQUAL, "ac")), diffs);

    diffs = diffList(new Diff(EQUAL, "c"), new Diff(INSERT, "ab"), new Diff(EQUAL, "a"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Slide edit right.", diffList(new Diff(EQUAL, "ca"), new Diff(INSERT, "ba")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(EQUAL, "c"), new Diff(DELETE, "ac"), new Diff(EQUAL, "x"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Slide edit left recursive.", diffList(new Diff(DELETE, "abc"), new Diff(EQUAL, "acx")), diffs);

    diffs = diffList(new Diff(EQUAL, "x"), new Diff(DELETE, "ca"), new Diff(EQUAL, "c"), new Diff(DELETE, "b"), new Diff(EQUAL, "a"));
    dmp.diff_cleanupMerge(diffs);
    assertEquals("diff_cleanupMerge: Slide edit right recursive.", diffList(new Diff(EQUAL, "xca"), new Diff(DELETE, "cba")), diffs);
  }

  public void testDiffCleanupSemanticLossless() {
    // Slide diffs to match logical boundaries.
    LinkedList<Diff> diffs = diffList();
    dmp.diff_cleanupSemanticLossless(diffs);
    assertEquals("diff_cleanupSemanticLossless: Null case.", diffList(), diffs);

    diffs = diffList(new Diff(EQUAL, "AAA\r\n\r\nBBB"), new Diff(INSERT, "\r\nDDD\r\n\r\nBBB"), new Diff(EQUAL, "\r\nEEE"));
    dmp.diff_cleanupSemanticLossless(diffs);
    assertEquals("diff_cleanupSemanticLossless: Blank lines.", diffList(new Diff(EQUAL, "AAA\r\n\r\n"), new Diff(INSERT, "BBB\r\nDDD\r\n\r\n"), new Diff(EQUAL, "BBB\r\nEEE")), diffs);

    diffs = diffList(new Diff(EQUAL, "AAA\r\nBBB"), new Diff(INSERT, " DDD\r\nBBB"), new Diff(EQUAL, " EEE"));
    dmp.diff_cleanupSemanticLossless(diffs);
    assertEquals("diff_cleanupSemanticLossless: Line boundaries.", diffList(new Diff(EQUAL, "AAA\r\n"), new Diff(INSERT, "BBB DDD\r\n"), new Diff(EQUAL, "BBB EEE")), diffs);

    diffs = diffList(new Diff(EQUAL, "The c"), new Diff(INSERT, "ow and the c"), new Diff(EQUAL, "at."));
    dmp.diff_cleanupSemanticLossless(diffs);
    assertEquals("diff_cleanupSemanticLossless: Word boundaries.", diffList(new Diff(EQUAL, "The "), new Diff(INSERT, "cow and the "), new Diff(EQUAL, "cat.")), diffs);

    diffs = diffList(new Diff(EQUAL, "The-c"), new Diff(INSERT, "ow-and-the-c"), new Diff(EQUAL, "at."));
    dmp.diff_cleanupSemanticLossless(diffs);
    assertEquals("diff_cleanupSemanticLossless: Alphanumeric boundaries.", diffList(new Diff(EQUAL, "The-"), new Diff(INSERT, "cow-and-the-"), new Diff(EQUAL, "cat.")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "a"), new Diff(EQUAL, "ax"));
    dmp.diff_cleanupSemanticLossless(diffs);
    assertEquals("diff_cleanupSemanticLossless: Hitting the start.", diffList(new Diff(DELETE, "a"), new Diff(EQUAL, "aax")), diffs);

    diffs = diffList(new Diff(EQUAL, "xa"), new Diff(DELETE, "a"), new Diff(EQUAL, "a"));
    dmp.diff_cleanupSemanticLossless(diffs);
    assertEquals("diff_cleanupSemanticLossless: Hitting the end.", diffList(new Diff(EQUAL, "xaa"), new Diff(DELETE, "a")), diffs);
  }

  public void testDiffCleanupSemantic() {
    // Cleanup semantically trivial equalities.
    LinkedList<Diff> diffs = diffList();
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: Null case.", diffList(), diffs);

    diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "cd"), new Diff(EQUAL, "12"), new Diff(DELETE, "e"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: No elimination #1.", diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "cd"), new Diff(EQUAL, "12"), new Diff(DELETE, "e")), diffs);

    diffs = diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "ABC"), new Diff(EQUAL, "1234"), new Diff(DELETE, "wxyz"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: No elimination #2.", diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "ABC"), new Diff(EQUAL, "1234"), new Diff(DELETE, "wxyz")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(EQUAL, "b"), new Diff(DELETE, "c"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: Simple elimination.", diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "b")), diffs);

    diffs = diffList(new Diff(DELETE, "ab"), new Diff(EQUAL, "cd"), new Diff(DELETE, "e"), new Diff(EQUAL, "f"), new Diff(INSERT, "g"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: Backpass elimination.", diffList(new Diff(DELETE, "abcdef"), new Diff(INSERT, "cdfg")), diffs);

    diffs = diffList(new Diff(INSERT, "1"), new Diff(EQUAL, "A"), new Diff(DELETE, "B"), new Diff(INSERT, "2"), new Diff(EQUAL, "_"), new Diff(INSERT, "1"), new Diff(EQUAL, "A"), new Diff(DELETE, "B"), new Diff(INSERT, "2"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: Multiple elimination.", diffList(new Diff(DELETE, "AB_AB"), new Diff(INSERT, "1A2_1A2")), diffs);

    diffs = diffList(new Diff(EQUAL, "The c"), new Diff(DELETE, "ow and the c"), new Diff(EQUAL, "at."));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: Word boundaries.", diffList(new Diff(EQUAL, "The "), new Diff(DELETE, "cow and the "), new Diff(EQUAL, "cat.")), diffs);

    diffs = diffList(new Diff(DELETE, "abcxx"), new Diff(INSERT, "xxdef"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: No overlap elimination.", diffList(new Diff(DELETE, "abcxx"), new Diff(INSERT, "xxdef")), diffs);

    diffs = diffList(new Diff(DELETE, "abcxxx"), new Diff(INSERT, "xxxdef"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: Overlap elimination.", diffList(new Diff(DELETE, "abc"), new Diff(EQUAL, "xxx"), new Diff(INSERT, "def")), diffs);

    diffs = diffList(new Diff(DELETE, "abcd1212"), new Diff(INSERT, "1212efghi"), new Diff(EQUAL, "----"), new Diff(DELETE, "A3"), new Diff(INSERT, "3BC"));
    dmp.diff_cleanupSemantic(diffs);
    assertEquals("diff_cleanupSemantic: Two overlap eliminations.", diffList(new Diff(DELETE, "abcd"), new Diff(EQUAL, "1212"), new Diff(INSERT, "efghi"), new Diff(EQUAL, "----"), new Diff(DELETE, "A"), new Diff(EQUAL, "3"), new Diff(INSERT, "BC")), diffs);
  }

  public void testDiffPrettyHtml() {
    // Pretty print.
    LinkedList<Diff> diffs = diffList(new Diff(EQUAL, "a\n"), new Diff(DELETE, "<B>b</B>"), new Diff(INSERT, "c&d"));
    assertEquals("diff_prettyHtml:", "<span>a&para;<br></span><del style=\"background:#ffe6e6;\">&lt;B&gt;b&lt;/B&gt;</del><ins style=\"background:#e6ffe6;\">c&amp;d</ins>", dmp.diff_prettyHtml(diffs));
  }

  public void testDiffBisect() {
    // Normal.
    String a = "cat";
    String b = "map";
    // Since the resulting diff hasn't been normalized, it would be ok if
    // the insertion and deletion pairs are swapped.
    // If the order changes, tweak this test as required.
    LinkedList<Diff> diffs = diffList(new Diff(DELETE, "c"), new Diff(INSERT, "m"), new Diff(EQUAL, "a"), new Diff(DELETE, "t"), new Diff(INSERT, "p"));
    assertEquals("diff_bisect: Normal.", diffs, dmp.diff_bisect(a, b, Long.MAX_VALUE));

    // Timeout.
    diffs = diffList(new Diff(DELETE, "cat"), new Diff(INSERT, "map"));
    assertEquals("diff_bisect: Timeout.", diffs, dmp.diff_bisect(a, b, 0));
  }

  public void testDiffMain() {
    // Perform a trivial diff.
    LinkedList<Diff> diffs = diffList();
    assertEquals("diff_main: Null case.", diffs, dmp.diff_main("", "", false));

    diffs = diffList(new Diff(EQUAL, "abc"));
    assertEquals("diff_main: Equality.", diffs, dmp.diff_main("abc", "abc", false));

    diffs = diffList(new Diff(EQUAL, "ab"), new Diff(INSERT, "123"), new Diff(EQUAL, "c"));
    assertEquals("diff_main: Simple insertion.", diffs, dmp.diff_main("abc", "ab123c", false));

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "123"), new Diff(EQUAL, "bc"));
    assertEquals("diff_main: Simple deletion.", diffs, dmp.diff_main("a123bc", "abc", false));

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(INSERT, "123"), new Diff(EQUAL, "b"), new Diff(INSERT, "456"), new Diff(EQUAL, "c"));
    assertEquals("diff_main: Two insertions.", diffs, dmp.diff_main("abc", "a123b456c", false));

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "123"), new Diff(EQUAL, "b"), new Diff(DELETE, "456"), new Diff(EQUAL, "c"));
    assertEquals("diff_main: Two deletions.", diffs, dmp.diff_main("a123b456c", "abc", false));

    // Perform a real diff.
    // Switch off the timeout.
    dmp.Diff_Timeout = 0;
    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "b"));
    assertEquals("diff_main: Simple case #1.", diffs, dmp.diff_main("a", "b", false));

    diffs = diffList(new Diff(DELETE, "Apple"), new Diff(INSERT, "Banana"), new Diff(EQUAL, "s are a"), new Diff(INSERT, "lso"), new Diff(EQUAL, " fruit."));
    assertEquals("diff_main: Simple case #2.", diffs, dmp.diff_main("Apples are a fruit.", "Bananas are also fruit.", false));

    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "\u0680"), new Diff(EQUAL, "x"), new Diff(DELETE, "\t"), new Diff(INSERT, "\000"));
    assertEquals("diff_main: Simple case #3.", diffs, dmp.diff_main("ax\t", "\u0680x\000", false));

    diffs = diffList(new Diff(DELETE, "1"), new Diff(EQUAL, "a"), new Diff(DELETE, "y"), new Diff(EQUAL, "b"), new Diff(DELETE, "2"), new Diff(INSERT, "xab"));
    assertEquals("diff_main: Overlap #1.", diffs, dmp.diff_main("1ayb2", "abxab", false));

    diffs = diffList(new Diff(INSERT, "xaxcx"), new Diff(EQUAL, "abc"), new Diff(DELETE, "y"));
    assertEquals("diff_main: Overlap #2.", diffs, dmp.diff_main("abcy", "xaxcxabc", false));

    diffs = diffList(new Diff(DELETE, "ABCD"), new Diff(EQUAL, "a"), new Diff(DELETE, "="), new Diff(INSERT, "-"), new Diff(EQUAL, "bcd"), new Diff(DELETE, "="), new Diff(INSERT, "-"), new Diff(EQUAL, "efghijklmnopqrs"), new Diff(DELETE, "EFGHIJKLMNOefg"));
    assertEquals("diff_main: Overlap #3.", diffs, dmp.diff_main("ABCDa=bcd=efghijklmnopqrsEFGHIJKLMNOefg", "a-bcd-efghijklmnopqrs", false));

    diffs = diffList(new Diff(INSERT, " "), new Diff(EQUAL, "a"), new Diff(INSERT, "nd"), new Diff(EQUAL, " [[Pennsylvania]]"), new Diff(DELETE, " and [[New"));
    assertEquals("diff_main: Large equality.", diffs, dmp.diff_main("a [[Pennsylvania]] and [[New", " and [[Pennsylvania]]", false));

    dmp.Diff_Timeout = 0.1f;  // 100ms
    String a = "`Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.\n";
    String b = "I am the very model of a modern major general,\nI've information vegetable, animal, and mineral,\nI know the kings of England, and I quote the fights historical,\nFrom Marathon to Waterloo, in order categorical.\n";
    // Increase the text lengths by 1024 times to ensure a timeout.
    for (int x = 0; x < 10; x++) {
      a = a + a;
      b = b + b;
    }
    long startTime = System.currentTimeMillis();
    dmp.diff_main(a, b);
    long endTime = System.currentTimeMillis();
    // Test that we took at least the timeout period.
    assertTrue("diff_main: Timeout min.", dmp.Diff_Timeout * 1000 <= endTime - startTime);
    // Test that we didn't take forever (be forgiving).
    // Theoretically this test could fail very occasionally if the
    // OS task swaps or locks up for a second at the wrong moment.
    assertTrue("diff_main: Timeout max.", dmp.Diff_Timeout * 1000 * 2 > endTime - startTime);
    dmp.Diff_Timeout = 0;

    // Test the linemode speedup.
    // Must be long to pass the 100 char cutoff.
    a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
    b = "abcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\n";
    assertEquals("diff_main: Simple line-mode.", dmp.diff_main(a, b, true), dmp.diff_main(a, b, false));

    a = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
    b = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij";
    assertEquals("diff_main: Single line-mode.", dmp.diff_main(a, b, true), dmp.diff_main(a, b, false));

    a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
    b = "abcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n";
    String[] texts_linemode = diff_rebuildtexts(dmp.diff_main(a, b, true));
    String[] texts_textmode = diff_rebuildtexts(dmp.diff_main(a, b, false));
    assertArrayEquals("diff_main: Overlap line-mode.", texts_textmode, texts_linemode);

    // Test null inputs.
    try {
      dmp.diff_main(null, null);
      fail("diff_main: Null inputs.");
    } catch (IllegalArgumentException ex) {
      // Error expected.
    }
  }


  private void assertArrayEquals(String error_msg, Object[] a, Object[] b) {
    List<Object> list_a = Arrays.asList(a);
    List<Object> list_b = Arrays.asList(b);
    assertEquals(error_msg, list_a, list_b);
  }


  private void assertLinesToCharsResultEquals(String error_msg,
      LinesToCharsResult a, LinesToCharsResult b) {
    assertEquals(error_msg, a.chars1, b.chars1);
    assertEquals(error_msg, a.chars2, b.chars2);
    assertEquals(error_msg, a.lineArray, b.lineArray);
  }

  // Construct the two texts which made up the diff originally.
  private static String[] diff_rebuildtexts(LinkedList<Diff> diffs) {
    String[] text = {"", ""};
    for (Diff myDiff : diffs) {
      if (myDiff.operation != DiffMatch.Operation.INSERT) {
        text[0] += myDiff.text;
      }
      if (myDiff.operation != DiffMatch.Operation.DELETE) {
        text[1] += myDiff.text;
      }
    }
    return text;
  }


  // Private function for quickly building lists of diffs.
  private static LinkedList<Diff> diffList(Diff... diffs) {
    LinkedList<Diff> myDiffList = new LinkedList<Diff>();
    for (Diff myDiff : diffs) {
      myDiffList.add(myDiff);
    }
    return myDiffList;
  }
}
