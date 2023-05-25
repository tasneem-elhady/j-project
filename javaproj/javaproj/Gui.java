package javaproj;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.events.MouseEvent;

public class Gui extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {

        Document doc = MDLFile.readFile("C:\\Users\\xtreme\\Downloads\\untitled.mdl");
        Block [] blocks= MDLFile.assembleBlocks(doc);
        Line[] lines = MDLFile.assembleLines(doc);
         for(int i =0 ;i<blocks.length;i++ ) {
         blocks[i].getCoordinates_Ports();
         }
         
        BlockPane myBlockPane = new BlockPane(blocks);
       ConnectorPane myConnectors = new ConnectorPane(lines,blocks);
       BorderPane border = new BorderPane();
       
       StackPane Root = new StackPane(myConnectors,myBlockPane);
       border.setCenter(Root);
        Scene sc = new Scene(border,1200,700);
        primaryStage.setTitle("View Shapes");
        primaryStage.setScene(sc);
        primaryStage.show();   
     }

         public static void main(String[] args)
        {
            System.out.println("Launching App");
            launch(args);
        }

}

class ConnectorPane extends Pane{
    public ConnectorPane(Line lines[],Block blocks[]){
        // Polyline p = new Polyline(null);
        // p.getPoints().addAll(new Double[]{        
        //     780.0 , 200.0,
             
        //     935.0, 217.0 
        //  });
        //  this.getChildren().add(p);
        for(Line l : lines)
        {
            l.getCoordinates();
            DrawLine(l, blocks);
            if(l.BranchesFromLine != null)
            {
                for(Line branch : l.BranchesFromLine)
                {
                    branch.getCoordinates();
                    Block dst = null;
                    for(Block b : blocks)
                    {
                        if(branch.hasDst && b.SID.equals(branch.dstBlock))
                        {
                            dst = b;
                        }
                    }
                    
                    Polyline p =new Polyline(null);
                    //double x = dst.getInputPortsY()[branch.inputPort-1] - l.getEndPointY();

                    p.getPoints().addAll(
                        l.getEndPointX(), l.getEndPointY(),
                        l.getEndPointX(), dst.getInputPortsY()[branch.inputPort-1],
                        dst.getInputPortsX()[branch.inputPort-1],
                        dst.getInputPortsY()[branch.inputPort-1]
                    );
                    this.getChildren().add(p);
                
                    CreateArrowHead(
                        dst.getInputPortsX()[branch.inputPort-1],
                        dst.getInputPortsY()[branch.inputPort-1], dst.mirrorred);
                }
            }

            //Polyline polyline = new Polyline();
            //Double points [] = Arrays.copyOf(l.Coordinates,l.Coordinates.length);
            // if(l.Coordinates != null){
            // for(int i = 0; i < l.Coordinates.length; i++)
            // {
            //     l.Coordinates[i] += src.getX();
            //     i++;
            //     l.Coordinates[i] += src.getY();
            // }
            // }
        }
    }
    public void DrawLine(Line l,Block blocks[])
    {
        for(Block b : blocks)
        {
            if(l.hasSrc && b.SID.equals(l.srcBlock))
            {
                l.src = b;
            }
            if(l.hasDst && b.SID.equals(l.dstBlock))
            {
                l.dst = b;
            }
        }
        if(l.hasSrc && l.hasDst && l.Coordinates == null)
        {
            javafx.scene.shape.Line connector = new javafx.scene.shape.Line(
                l.src.getOutputPortsX()[l.outputPort-1],
                l.src.getOutputPortsY()[l.outputPort-1],
                l.dst.getInputPortsX()[l.inputPort-1],
                l.dst.getInputPortsY()[l.inputPort-1]
            );

            CreateArrowHead(
                l.dst.getInputPortsX()[l.inputPort-1],
                l.dst.getInputPortsY()[l.inputPort-1],
                l.dst.mirrorred
            );
        this.getChildren().add(connector);

        }
        else if(l.hasSrc && l.hasDst && l.Coordinates != null)
        {
            Polyline p =new Polyline(null);
                    double x = l.src.getOutputPortsX()[l.outputPort-1];
                    double y = l.src.getOutputPortsY()[l.outputPort-1];
                    p.getPoints().addAll(
                        l.src.getOutputPortsX()[l.outputPort-1],
                        l.src.getOutputPortsY()[l.outputPort-1]
                    );
                    for(int i = 0;i < l.Coordinates.length;i++){
                        p.getPoints().addAll(
                            x+=l.Coordinates[i++],
                            y+=l.Coordinates[i]
                        );
                    }
                    p.getPoints().addAll(
                        l.dst.getInputPortsX()[l.inputPort-1],
                        l.dst.getInputPortsY()[l.inputPort-1]
                    );
                    this.getChildren().add(p);

            CreateArrowHead(
                l.dst.getInputPortsX()[l.inputPort-1],
                l.dst.getInputPortsY()[l.inputPort-1],
                l.dst.mirrorred
            );

        }
        else if(!l.hasDst && l.Coordinates.length == 2)
        {
            javafx.scene.shape.Line connector = new javafx.scene.shape.Line(
                l.src.getOutputPortsX()[l.outputPort-1],
                l.src.getOutputPortsY()[l.outputPort-1],
                l.src.getOutputPortsX()[l.outputPort-1] + (l.Coordinates[0]),
                l.src.getOutputPortsY()[l.outputPort-1] + (l.Coordinates[1])
            );
        this.getChildren().add(connector);

        }
        
    }
    public void CreateArrowHead(double endx, double endy, boolean mirrorred)
    {
        double arrowLength = 8;
        double arrowWidth = 8;
        // Add arrowheads to the line

        double angle = Math.atan2(0, endx) - Math.PI / 2.0;

        // Create the arrowhead polygon
        Polygon arrowhead = new Polygon();
        arrowhead.getPoints().addAll(
            0.0, 0.0,
            -arrowWidth / 2.0, arrowLength,
            arrowWidth / 2.0, arrowLength
        );

        arrowhead.setFill(Color.BLACK);

        // Translate and rotate the arrowhead to the end of the line
    if(mirrorred)
        {
            arrowhead.setRotate(angle * 180 / Math.PI);
            arrowhead.setTranslateX(endx + 4);
            arrowhead.setTranslateY(endy-arrowWidth/2);
        }
    else
    {
        arrowhead.setRotate(3*angle * 180 / Math.PI);
        arrowhead.setTranslateX(endx - 4);
        arrowhead.setTranslateY(endy-arrowWidth/2);
    }
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLUE);
        shadow.setRadius(10); // Set the shadow radius to 10 pixels
        arrowhead.setEffect(shadow);
        this.getChildren().add(arrowhead);
    }
}

class BlockPane extends Pane
{
    
     public BlockPane(Block[] blocks){
        for(Block b :blocks){
        
        Rectangle r= new Rectangle(b.getX(), b.getY(),b.getWidth(),b.getHeight());
         Tooltip tooltip = new Tooltip("This is all the attributes of the this Block : \n" +b.toString());
         Tooltip.install(r, tooltip);

         r.setFill(Color.WHITE);
         r.setStroke(Color.BLACK);
         DropShadow shadow = new DropShadow();
         shadow.setColor(Color.BLUE);
         shadow.setRadius(10); // Set the shadow radius to 10 pixels
         r.setEffect(shadow);
         this.getChildren().add(r);

         Text r_txt = new Text(b.getX(), b.getY()+b.getHeight()+10, b.name);
         r_txt.setFill(Color.BLACK);
         r_txt.setFont(Font.font("Arial", FontWeight.THIN, 10));
         this.getChildren().add(r_txt);

         for(int i =0 ;i<b.properties.length;i++){

            if(b.properties[i][0].equalsIgnoreCase("Inputs")){
                for(int j =0;j<b.properties[i][1].length();j++) {
                    Text plus = new Text(b.getX() + 2,b.getInputPortsY()[j] + 3 ,b.properties[i][1].charAt(j)+"");
                    plus.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                    plus.setFill(Color.BLACK);
                    this.getChildren().add(plus);}}


      } }}}



