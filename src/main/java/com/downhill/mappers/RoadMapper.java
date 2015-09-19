package com.downhill.mappers;

import com.downhill.models.LngLat;
import com.downhill.models.Road;
import com.downhill.models.RoadSegment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoadMapper implements ResultSetMapper<Road>
{
    private static String COLUMN_NAME = "roads";
    private static String COORDINATES = "coordinates";

    @Override
    public Road map( int i, ResultSet resultSet, StatementContext statementContext ) throws SQLException
    {
        JsonParser parser = new JsonParser();
        JsonElement viewBounds = parser.parse( resultSet.getString( COLUMN_NAME ) );
        JsonArray coordinates = viewBounds.getAsJsonObject().getAsJsonArray( COORDINATES );
        List<LngLat> points = new ArrayList<LngLat>();
        LngLat previousPoint = null;
        List<RoadSegment> segments = new ArrayList<>();
        for ( int j = 0; j < coordinates.size(); j++ )
        {
            JsonArray coord = coordinates.get( j ).getAsJsonArray();
            LngLat point = new LngLat( coord.get( 0 ).getAsDouble(), coord.get( 1 ).getAsDouble() );
            points.add( point );
            if ( previousPoint != null )
            {
                RoadSegment segment = new RoadSegment( previousPoint, point );
                segments.add( segment );
            }
            previousPoint = point;
        }
        return new Road( points, segments );
    }
}