from pathlib import Path
import re
from xml.sax.saxutils import escape
from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak

font_paths = [Path("C:/Windows/Fonts"), Path("/usr/share/fonts/truetype/dejavu")]
for folder in font_paths:
    regular = folder / ("arial.ttf" if folder.name == "Fonts" else "DejaVuSans.ttf")
    bold = folder / ("arialbd.ttf" if folder.name == "Fonts" else "DejaVuSans-Bold.ttf")
    if regular.exists() and bold.exists():
        pdfmetrics.registerFont(TTFont("Report", str(regular)))
        pdfmetrics.registerFont(TTFont("ReportBold", str(bold)))
        break
else:
    raise FileNotFoundError("Install Arial or DejaVu Sans to generate the report")

pdfmetrics.registerFontFamily("Report", normal="Report", bold="ReportBold")
body = ParagraphStyle("Body", fontName="Report", fontSize=9.4, leading=12.7,
                      spaceAfter=7, alignment=TA_LEFT)
title = ParagraphStyle("Title", parent=body, fontName="ReportBold", fontSize=17, leading=21, spaceAfter=8)
heading = ParagraphStyle("Heading", parent=body, fontName="ReportBold", fontSize=13, leading=17, spaceAfter=9)
subheading = ParagraphStyle("Subheading", parent=body, fontName="ReportBold", fontSize=10.5, leading=14, spaceBefore=4)
cell = ParagraphStyle("Cell", parent=body, fontSize=7.9, leading=10, spaceAfter=0)

def formatted(text):
    return re.sub(r"\*\*(.*?)\*\*", r"<b>\1</b>", escape(text))

lines = Path("REPORT.md").read_text(encoding="utf-8").splitlines()
story = []
section = 0
i = 0
while i < len(lines):
    line = lines[i]
    if not line.strip():
        i += 1
        continue
    if line.startswith("| "):
        rows = []
        while i < len(lines) and lines[i].startswith("| "):
            if not lines[i].startswith("| ---"):
                rows.append([Paragraph(formatted(value.strip()), cell) for value in lines[i].strip("|").split("|")])
            i += 1
        table = Table(rows, colWidths=[132, 45, 137, 99, 110], repeatRows=1)
        table.setStyle(TableStyle([
            ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#e8edf2")),
            ("GRID", (0, 0), (-1, -1), 0.35, colors.HexColor("#b6bec6")),
            ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ("TOPPADDING", (0, 0), (-1, -1), 4),
            ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
        ]))
        story.extend([table, Spacer(1, 9)])
        continue
    if line.startswith("!["):
        path = re.search(r"\]\((.*?)\)", line).group(1)
        story.append(Image(path, width=523, height=282.42))
        story.append(Spacer(1, 5))
    elif line.startswith("### "):
        story.append(Paragraph(formatted(line[4:]), subheading))
    elif line.startswith("## "):
        if section > 0:
            story.append(PageBreak())
        section += 1
        story.append(Paragraph(formatted(line[3:]), heading))
    elif line.startswith("# "):
        story.append(Paragraph(formatted(line[2:]), title))
    else:
        story.append(Paragraph(formatted(line), body))
    i += 1

SimpleDocTemplate("REPORT.pdf", pagesize=A4, leftMargin=36, rightMargin=36,
                  topMargin=30, bottomMargin=30, title="Assignment 2 - Data Structures",
                  author="Alua Rakhimzhanova").build(story)
