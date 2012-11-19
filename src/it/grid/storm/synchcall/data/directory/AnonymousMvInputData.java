package it.grid.storm.synchcall.data.directory;


import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousMvInputData extends AbstractInputData implements MvInputData
{

    private final TSURL fromSURL;
    private final TSURL toSURL;

    public AnonymousMvInputData(TSURL fromSURL, TSURL toSURL)throws IllegalArgumentException
    {
        if (fromSURL == null || toSURL == null)
        {
            throw new IllegalArgumentException("Unable to create the object, invalid arguments: fromSURL="
                    + fromSURL + " toSURL=" + toSURL);
        }
        this.fromSURL = fromSURL;
        this.toSURL = toSURL;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.directory.MvInputData#getToSURL()
     */
    @Override
    public TSURL getToSURL()
    {
        return toSURL;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.directory.MvInputData#getFromSURL()
     */
    @Override
    public TSURL getFromSURL()
    {
        return fromSURL;
    }

}
