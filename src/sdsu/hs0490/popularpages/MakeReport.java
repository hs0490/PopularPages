package sdsu.hs0490.popularpages;
/**
 * Created by hs0490 on 9/12/14.
 */


import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.sql.SQLException;
import java.util.SortedMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.JSONException;


public class MakeReport {

    /**
     * Generate Output
     * @param url facebook url
     * @param reportName the path to the new PDF document
     * @throws DocumentException
     * @throws IOException
     */
    public void generateOutput(String url, String reportName) throws IOException, DocumentException, JSONException {

        PageInfo aInfo = new PageInfo();
        aInfo.getPageInfo(url);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(reportName));
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
        writer.close();

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
        PrintWriter fileWriter = new PrintWriter("talking_about_count.txt");
        fileWriter.println("Top 5 Pages with most Talking_About_Count");
        for(int i=1; i <= 5 ; i++ ){
            int talkingAboutCount =  talkingAboutCountMap.lastKey();
            String pageID =  talkingAboutCountMap.get(talkingAboutCount);
            String pageName = PageInfo.pagesTable.get(pageID);
            fileWriter.println(i+" "+pageName+"--> Talking_About_Count = "+talkingAboutCount);
            dataSet.setValue(pageName,talkingAboutCount);
            talkingAboutCountMap.remove(talkingAboutCount,pageID);
        }
        fileWriter.close();
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
        PrintWriter fileWriter = new PrintWriter("like_count.txt");
        fileWriter.println("Top 5 Pages with most likes");

        for(int i= 1; i <=5 ; i++ ){
            int likes = likeCountMap.lastKey();
            String pageID = likeCountMap.get(likes);
            String pageName = PageInfo.pagesTable.get(pageID);
            fileWriter.println(i+" "+pageName+"--> Likes = "+likes);
            dataSet.setValue(likes, "Likes", pageID);
            likeCountMap.remove(likes,pageID);
        }
        fileWriter.close();
        return ChartFactory.createBarChart3D(
                "Page Like Count", "Page ID", "Likes",
                dataSet, PlotOrientation.HORIZONTAL, false, true, false);
    }

}
