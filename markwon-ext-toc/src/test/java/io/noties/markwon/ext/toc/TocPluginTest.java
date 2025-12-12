package io.noties.markwon.ext.toc;

import org.commonmark.node.BulletList;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests for TocPlugin functionality.
 */
public class TocPluginTest {

    @Test
    public void testTocBlockParsing() {
        // Test that [TOC] is parsed as a TocBlock
        final Parser parser = Parser.builder()
                .customBlockParserFactory(new TocBlockParser.Factory())
                .build();

        final String markdown = "# Heading 1\n\n[TOC]\n\n## Heading 2\n";
        final Document document = parser.parse(markdown);

        // Find the TocBlock
        TocBlock tocBlock = findTocBlock(document);
        assertNotNull("TocBlock should be parsed", tocBlock);
    }

    @Test
    public void testTocBlockCaseInsensitive() {
        // Test that [toc] (lowercase) is also recognized
        final Parser parser = Parser.builder()
                .customBlockParserFactory(new TocBlockParser.Factory())
                .build();

        final String markdown = "[toc]\n";
        final Document document = parser.parse(markdown);

        TocBlock tocBlock = findTocBlock(document);
        assertNotNull("TocBlock should be parsed (case-insensitive)", tocBlock);
    }

    @Test
    public void testTocBlockNotParsedWithExtraContent() {
        // Test that [TOC] with extra content is not parsed as TocBlock
        final Parser parser = Parser.builder()
                .customBlockParserFactory(new TocBlockParser.Factory())
                .build();

        final String markdown = "[TOC] extra content\n";
        final Document document = parser.parse(markdown);

        TocBlock tocBlock = findTocBlock(document);
        assertNull("TocBlock should not be parsed with extra content", tocBlock);
    }

    @Test
    public void testTocPopulation() {
        // Test that TocBlock is populated with headings
        final Parser parser = Parser.builder()
                .customBlockParserFactory(new TocBlockParser.Factory())
                .build();

        final String markdown = "# Heading 1\n\n[TOC]\n\n## Heading 2\n\n# Heading 3\n";
        final Document document = parser.parse(markdown);

        // Apply the beforeRender logic
        TocPlugin plugin = TocPlugin.create();
        plugin.beforeRender(document);

        // Find the TocBlock and check it has children
        TocBlock tocBlock = findTocBlock(document);
        assertNotNull("TocBlock should be parsed", tocBlock);
        
        // TocBlock should now have a BulletList child
        Node firstChild = tocBlock.getFirstChild();
        assertNotNull("TocBlock should have children after beforeRender", firstChild);
        assertTrue("First child should be BulletList", firstChild instanceof BulletList);
    }

    private TocBlock findTocBlock(Node node) {
        if (node instanceof TocBlock) {
            return (TocBlock) node;
        }
        for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
            TocBlock found = findTocBlock(child);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
