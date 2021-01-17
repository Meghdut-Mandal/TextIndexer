public interface IndexingProgressListener
{
    /**
     * Receive an update which wad just read
     * @param fileDocument  the file which was just read
     * @param currentCount total number of files read till now
     */
    void update(FileDocument fileDocument,long currentCount);
}
