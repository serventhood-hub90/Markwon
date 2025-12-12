package io.noties.markwon.ext.toc;

import androidx.annotation.NonNull;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.core.SimpleBlockNodeVisitor;

/**
 * Plugin to support Gitiles-style [TOC] directive in markdown documents.
 * 
 * <p>When a line containing only [TOC] is found, it will be replaced with a table of contents
 * that lists all headings in the document with clickable links.</p>
 * 
 * <p>Usage:</p>
 * <pre>{@code
 * Markwon markwon = Markwon.builder(context)
 *     .usePlugin(TocPlugin.create())
 *     .build();
 * }</pre>
 *
 * <p>Example markdown:</p>
 * <pre>{@code
 * # Introduction
 * Some content here.
 * 
 * [TOC]
 * 
 * # Chapter 1
 * More content.
 * 
 * ## Section 1.1
 * Details.
 * }</pre>
 * 
 * <p>The [TOC] will be replaced with:</p>
 * <ul>
 *   <li>Introduction</li>
 *   <li>Chapter 1
 *     <ul>
 *       <li>Section 1.1</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * @see TocBlock
 * @see TocBlockParser
 * @since 4.7.0
 */
public class TocPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static TocPlugin create() {
        return new TocPlugin();
    }

    private TocPlugin() {
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customBlockParserFactory(new TocBlockParser.Factory());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        // Register a simple visitor that just renders children (the bullet list we'll inject)
        builder.on(TocBlock.class, new SimpleBlockNodeVisitor());
    }

    @Override
    public void beforeRender(@NonNull Node node) {
        // Find all TocBlock nodes and populate them with table of contents
        node.accept(new AbstractVisitor() {
            @Override
            public void visit(TocBlock tocBlock) {
                // Collect all headings from the document
                final HeadingCollector collector = new HeadingCollector();
                // Visit the root node to collect all headings
                Node root = tocBlock;
                while (root.getParent() != null) {
                    root = root.getParent();
                }
                root.accept(collector);

                // Add the collected TOC as children of this TocBlock
                if (collector.bulletList.getFirstChild() != null) {
                    tocBlock.appendChild(collector.bulletList);
                }
            }
        });
    }

    /**
     * Visitor that collects all headings and builds a nested bullet list.
     * 
     * This implementation creates a simple flat list structure where heading levels
     * are represented by indentation (nested lists). Each heading becomes a list item
     * with a link, and deeper levels are nested within parent items.
     */
    private static class HeadingCollector extends AbstractVisitor {
        final BulletList bulletList = new BulletList();
        private final StringBuilder builder = new StringBuilder();
        private boolean isInsideHeading;

        @Override
        public void visit(Heading heading) {
            this.isInsideHeading = true;
            try {
                // Reset builder from previous content
                builder.setLength(0);

                // Get heading level (1-6)
                final int level = heading.getLevel();

                // Build heading title by visiting children
                visitChildren(heading);
                final String content = builder.toString();

                // Create the list item that will contain this heading
                final ListItem listItem = new ListItem();

                // Navigate to the correct nesting level
                // Level 1 headings go directly in bulletList
                // Level 2+ headings need nested lists
                Node targetNode = listItem;
                
                for (int i = 1; i < level; i++) {
                    // Create nested structure for deeper levels
                    final ListItem nestedLi = new ListItem();
                    final BulletList nestedList = new BulletList();
                    nestedList.appendChild(nestedLi);
                    targetNode.appendChild(nestedList);
                    targetNode = nestedLi;
                }

                // Create the link with heading content
                final Link link = new Link("#" + createAnchor(content), null);
                final Text text = new Text(content);
                link.appendChild(text);
                targetNode.appendChild(link);
                
                // Add to the root bullet list
                bulletList.appendChild(listItem);

            } finally {
                isInsideHeading = false;
            }
        }

        @Override
        public void visit(Text text) {
            // Collect text only when building heading content
            if (isInsideHeading) {
                builder.append(text.getLiteral());
            }
        }

        @Override
        public void visit(TocBlock tocBlock) {
            // Don't visit TOC blocks when collecting headings
            // (avoid infinite recursion and including TOC in itself)
        }
    }

    /**
     * Creates an anchor ID from heading content.
     * Follows a simple convention: lowercase, remove non-word characters, replace spaces with hyphens.
     * 
     * Note: Duplicate headings will produce duplicate anchor IDs. When clicked, the link will
     * navigate to the first occurrence. This is consistent with standard TOC behavior.
     * 
     * @param content the heading text
     * @return anchor string suitable for linking
     */
    @NonNull
    private static String createAnchor(@NonNull String content) {
        return content.replaceAll("[^\\w\\s]", "")
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }
}
