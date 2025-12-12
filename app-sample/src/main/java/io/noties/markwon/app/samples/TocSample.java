package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.plugins.shared.AnchorHeadingPlugin;
import io.noties.markwon.ext.toc.TocPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20251212141100",
  title = "[TOC] Directive",
  description = "Gitiles-style [TOC] directive for automatic table of contents generation",
  artifacts = {MarkwonArtifact.CORE, MarkwonArtifact.EXT_TOC},
  tags = {Tag.rendering, Tag.plugin}
)
public class TocSample extends MarkwonTextViewSample {

  @Override
  public void render() {
    final String lorem = context.getString(R.string.lorem);
    
    // Example markdown with [TOC] directive
    final String md = "" +
      "# Introduction\n" +
      "\n" +
      "This document demonstrates the [TOC] directive.\n" +
      "\n" +
      "[TOC]\n" +
      "\n" +
      "# Chapter 1\n" +
      "" + lorem + "\n\n" +
      "## Section 1.1\n" +
      "First section content.\n\n" +
      "## Section 1.2\n" +
      "Second section content.\n\n" +
      "# Chapter 2\n" +
      "" + lorem + "\n\n" +
      "## Section 2.1\n" +
      "Another section.\n\n" +
      "### Subsection 2.1.1\n" +
      "Deeper nesting example.\n\n" +
      "# Conclusion\n" +
      "" + lorem + "\n";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TocPlugin.create())
      // Optional: enable anchor scrolling for clickable TOC links
      .usePlugin(new AnchorHeadingPlugin((view, top) -> scrollView.smoothScrollTo(0, top)))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
