package markdownparser;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
public class MarkdownParser
{
    Parser parser;
    HtmlRenderer renderer;

    public MarkdownParser()
    {
        parser = Parser.builder().build();
        renderer = HtmlRenderer.builder().build();
    }

    public String convertToMarkdown(String markdown)
    {
        String html = renderer.render(parser.parse(markdown));
        return html;
    }
 };
