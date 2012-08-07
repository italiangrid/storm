package it.grid.storm.asynch;

public interface CopyVisitor
{

    public Copy.Result visit(VisitableCopy copy);
}
