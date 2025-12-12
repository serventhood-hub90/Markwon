package io.noties.markwon.ext.toc;

import androidx.annotation.NonNull;

import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

/**
 * Parser that detects [TOC] directive in markdown and creates a TocBlock node.
 * 
 * The [TOC] directive must be on its own line (with optional leading/trailing whitespace).
 *
 * @since 4.7.0
 */
public class TocBlockParser extends AbstractBlockParser {

    private final TocBlock block = new TocBlock();
    private boolean finished = false;

    @NonNull
    @Override
    public Block getBlock() {
        return block;
    }

    @NonNull
    @Override
    public BlockContinue tryContinue(@NonNull ParserState parserState) {
        // [TOC] is a single-line directive, so we're done after first line
        if (finished) {
            return BlockContinue.none();
        }
        finished = true;
        return BlockContinue.finished();
    }

    public static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(@NonNull ParserState state, @NonNull MatchedBlockParser matchedBlockParser) {
            final CharSequence line = state.getLine();
            final String trimmed = line.toString().trim();

            // Check if the line is exactly [TOC] (case-insensitive as per Gitiles)
            if (trimmed.equalsIgnoreCase("[TOC]")) {
                return BlockStart.of(new TocBlockParser()).atIndex(line.length());
            }

            return BlockStart.none();
        }
    }
}
