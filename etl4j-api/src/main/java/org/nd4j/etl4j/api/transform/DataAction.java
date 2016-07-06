package org.nd4j.etl4j.api.transform;

import lombok.Data;
import org.nd4j.etl4j.api.transform.filter.Filter;
import org.nd4j.etl4j.api.transform.rank.CalculateSortedRank;
import org.nd4j.etl4j.api.transform.reduce.IReducer;
import org.nd4j.etl4j.api.transform.schema.Schema;
import org.nd4j.etl4j.api.transform.sequence.ConvertToSequence;
import org.nd4j.etl4j.api.transform.sequence.SequenceSplit;
import org.nd4j.etl4j.api.transform.sequence.ConvertFromSequence;

import java.io.Serializable;

/** A helper class used in TransformProcess to store the types of action to execute next. */
@Data
public class DataAction implements Serializable {

    private Transform transform;
    private Filter filter;
    private ConvertToSequence convertToSequence;
    private ConvertFromSequence convertFromSequence;
    private SequenceSplit sequenceSplit;
    private IReducer reducer;
    private CalculateSortedRank calculateSortedRank;

    public DataAction(Transform transform) {
        this.transform = transform;
    }

    public DataAction(Filter filter) {
        this.filter = filter;
    }

    public DataAction(ConvertToSequence convertToSequence) {
        this.convertToSequence = convertToSequence;
    }

    public DataAction(ConvertFromSequence convertFromSequence) {
        this.convertFromSequence = convertFromSequence;
    }

    public DataAction(SequenceSplit sequenceSplit){
        this.sequenceSplit = sequenceSplit;
    }

    public DataAction(IReducer reducer){
        this.reducer = reducer;
    }

    public DataAction(CalculateSortedRank calculateSortedRank){
        this.calculateSortedRank = calculateSortedRank;
    }

    @Override
    public String toString(){
        String str;
        if(transform != null){
            str = transform.toString();
        } else if(filter != null){
            str = filter.toString();
        } else if(convertToSequence != null){
            str = convertToSequence.toString();
        } else if(convertFromSequence != null){
            str = convertFromSequence.toString();
        } else if(sequenceSplit != null) {
            str = sequenceSplit.toString();
        } else if(reducer != null) {
            str = reducer.toString();
        } else if(calculateSortedRank != null) {
            str = calculateSortedRank.toString();
        } else {
            throw new IllegalStateException("Invalid DataAction: does not contain any operation to perform (all fields are null)");
        }
        return "DataAction(" + str + ")";
    }

    public Schema getSchema(){
        if(transform != null){
            return transform.getInputSchema();
        } else if(filter != null){
            return filter.getInputSchema();
        } else if(convertToSequence != null){
            return convertToSequence.getInputSchema();
        } else if(convertFromSequence != null){
            return convertFromSequence.getInputSchema();
        } else if(sequenceSplit != null) {
            return sequenceSplit.getInputSchema();
        } else if(reducer != null) {
            return reducer.getInputSchema();
        } else if(calculateSortedRank != null) {
            return calculateSortedRank.getInputSchema();
        } else {
            throw new IllegalStateException("Invalid DataAction: does not contain any operation to perform (all fields are null)");
        }
    }

}