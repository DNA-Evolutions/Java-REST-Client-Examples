package com.dna.jopt.rest.client.util.helper;

import java.time.Duration;

import com.dna.jopt.rest.client.model.Position;

import lombok.Getter;
import lombok.Setter;

public class TripExtraction {


    @Setter
    @Getter
    private Position fromPos;
    
    
    @Setter
    @Getter
    private Position toPos;
    
    @Setter
    @Getter
    Duration time;
    
    @Setter
    @Getter
    Double distance;
    
    
    @Setter
    @Getter
    String shape;
    
    
    @Override
    public String toString() {
	
	 StringBuilder builder = new StringBuilder();
	 
	 builder.append("\nFrom: "+fromPos + " == " + "To: "+ toPos);
	 builder.append("\nDistance: "+distance + " Time: "+ time + "\n");
	 
	 return builder.toString();
    }

}
