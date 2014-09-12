/**
 * Created by hs0490 on 9/12/14.
 */


import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.sql.SQLException;
import java.util.SortedMap;

import com.orsoncharts.plot.CategoryPlot3D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.ui.TextAnchor;
import org.json.JSONException;


public class MakeReport {

    private String url;
    private String fileName;
    private BufferedWriter fileWriter;

    public MakeReport(String url, String fileName){
        this.url = url;
        this.fileName = fileName;
    }

    /**
     * Creates a PDF document.
     * @param filename the path to the new PDF document
     * @throws DocumentException
     * @throws IOException
     * @throws SQLException
     */
    public void createPdf(String filename) throws IOException, DocumentException, SQLException, JSONException {

        PageInfo aInfo = new PageInfo();
        aInfo.getPageInfo(url);

        //Results file
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        fileWriter= new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        document.addTitle("Community Pages");
        PdfContentByte cb = writer.getDirectContent();
        float width = PageSize.A4.getWidth();
        float height = PageSize.A4.getHeight() / 2;

        // Pie chart
        PdfTemplate pie = cb.createTemplate(width, height);
        Graphics2D g2d1 = new PdfGraphics2D(pie, width, height);
        Rectangle2D r2d1 = new Rectangle2D.Double(0, 0, width, height);
        JFreeChart pieChart = getPieChart(PageInfo.talkingAboutCountMap);
        pieChart.draw(g2d1, r2d1);
        g2d1.dispose();
        cb.addTemplate(pie, 0, height);

        // Bar chart
        PdfTemplate bar = cb.createTemplate(width, height);
        Graphics2D g2d2 = new PdfGraphics2D(bar, width, height);
        Rectangle2D r2d2 = new Rectangle2D.Double(0, 0, width, height);
        JFreeChart barChart = getBarChart(PageInfo.likeCountMap);
        barChart.draw(g2d2, r2d2);
        g2d2.dispose();
        cb.addTemplate(bar, 0, 0);

        document.close();

    }


    /**
     * Gets a pie chart.
     *
     * @return a pie chart
     * @throws IOException
     * @param talkingAboutCountMap
     */
    public  JFreeChart getPieChart(SortedMap<Integer, String> talkingAboutCountMap) throws IOException {
        DefaultPieDataset dataSet = new DefaultPieDataset();
        fileWriter.write("Top 5 Pages with most Talking_About_Count");
        fileWriter.newLine();
        for(int i= 0; i < 5 ; i++ ){
            int talkingAboutCount =  talkingAboutCountMap.lastKey();
            String pageID =  talkingAboutCountMap.get(talkingAboutCount);
            String pageName = PageInfo.pagesTable.get(pageID);
            dataSet.setValue(pageName,talkingAboutCount);
            talkingAboutCountMap.remove(talkingAboutCount,pageID);
            String content = i+" "+pageName+"--> Talking_About_Count = "+talkingAboutCount;
            fileWriter.write(content);
        }
        return ChartFactory.createPieChart3D("Talking About Count", dataSet,
                true, true, false);
    }


    /**
     * Get bar chart.
     *
     * @return a bar chart
     * @throws IOException
     * @param likeCountMap
     */
    public JFreeChart getBarChart(SortedMap<Integer, String> likeCountMap) throws IOException {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        fileWriter.write("Top 5 Pages with most likes");
        fileWriter.newLine();

        for(int i= 0; i < 5 ; i++ ){
            int likes = likeCountMap.lastKey();
            String pageID = likeCountMap.get(likes);
            String pageName = PageInfo.pagesTable.get(pageID);
            dataSet.setValue(likes, "Likes", pageID);
            likeCountMap.remove(likes,pageID);
            String content = i+" "+pageName+"--> Likes = "+likes;
            fileWriter.write(content);
            fileWriter.newLine();
        }
        return ChartFactory.createBarChart3D(
                "Pages Liked", "Pages", "Likes",
                dataSet, PlotOrientation.HORIZONTAL, false, true, false);
    }

}
