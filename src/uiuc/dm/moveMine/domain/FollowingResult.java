/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uiuc.dm.moveMine.domain;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author fxw133-admin
 */
public class FollowingResult {
    private String start_time;
	private String end_time;
	private String rel = "n/a";
	private String path;
	private double score;
	private int start;
	private int end;
        private String follower_id;
        private String leader_id;
        private int total_length;
        private long interval_length;

        public int get_total_length(){
            return this.total_length;
        }
        public void set_total_length(int length){
            this.total_length = length;
        }
        public long get_interal_length(){return this.interval_length;}
        public int get_start_index(){return this.start; }
        public String get_start_str(){return this.start_time; }
        public String get_end_str(){return this.end_time; }
        public int get_end_index(){return this.end;}
	
	public FollowingResult(int start, int end, double score,  Trajectory follower, Trajectory leader,int length){
		this.start       = start;
		this.end         = end;
		this.interval_length = (follower.getPoint(end).getTime().getMillis() - follower.getPoint(start).getTime().getMillis())/1000;
		this.score       = score;
                this.start_time  = follower.getPoint(start).getTime().toString("YYYY-MM-dd-HH:mm:ss");
		this.end_time    = follower.getPoint(end).getTime().toString("YYYY-MM-dd-HH:mm:ss");
		String follower_name = follower.getId();
		String leader_name   = leader.getId();
                this.follower_id     = follower.getId();
                this.leader_id       =  leader.getId();
		this.rel         = follower_name+"_follows_"+leader_name;
                this.total_length = length;
	}
        public double getScore(){
            return this.score;
        }
        public String get_follower(){
                return this.follower_id;
        }
        public String get_leader(){
                return this.leader_id;
        }
	public String get_rel(){
		return rel;
	}
	public String get_start_time(){
		return start_time;
	}
	public String get_end_time(){
		return end_time;
	}
	

	public double score(){
		return this.score;
	}

	public boolean export_as_kml(String path, Trajectory follower, Trajectory leader) throws IOException{
		String kml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>         <kml xmlns=\"http://earth.google.com/kml/2.0\"> " +
				"         <Document>" +
				"        <Style id =\"blue\"><LineStyle> <color>ffff0000</color><width>4</width></LineStyle>   " +
				" <IconStyle><scale>0.2</scale>			<Icon>				<href>Icon_Blue_Dot.png</href></Icon>		" +
				"</IconStyle></Style>        " +
				"<Style id =\"red\">        <LineStyle> <color>ff0000ff</color><width>4</width></LineStyle><IconStyle>" +
				"			<scale>0.2</scale>			<Icon>				<href>Icon_Red_Dot.png</href></Icon>" +
				"		</IconStyle>    </Style>";

		String leader_points   = "";
		String follower_points = "";
		for(int i = this.start; i < this.end ; i++){
			leader_points   += "<Placemark><styleUrl>#red</styleUrl>" +
							   "<TimeStamp><when>"+i+"</when></TimeStamp>"+
							   "<Point><coordinates>"+leader.getPoint(i).getX()+","+leader.getPoint(i).getY()+",20</coordinates></Point> </Placemark>\n";
			follower_points += "<Placemark><styleUrl>#blue</styleUrl>" +
					  		   "<TimeStamp><when>"+i+"</when></TimeStamp>"+
					  		   "<Point><coordinates>"+follower.getPoint(i).getX()+","+follower.getPoint(i).getY()+",40</coordinates></Point> </Placemark>\n";
	
		}
		String kml = kml_header + leader_points ;
		kml += follower_points;
		kml += "</Document></kml>";
		write_file(path,kml);
		return true;
	}
	public static boolean write_file(String path, String data) throws IOException{
		System.out.println(path);
		  Writer writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(path), "utf-8"));
		    writer.write(data);
		    writer.close();
		return true;
	}
	@Override 
	public String toString(){
		return this.start_time+"|"+this.end_time+"|"+this.rel+"|";
		
	}

}
