package ca.nait.dmit.batch;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.batch.runtime.context.JobContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.nio.file.Paths;

@Named
public class EnforcementZoneCentreItemReader extends AbstractItemReader {

    @Inject
    private JobContext _jobContext;

    private BufferedReader _reader;

    @Inject
    @BatchProperty(name = "input_file")
    private String inputFile;

    // alt+ent override method
    @Override
    public void open(Serializable checkpoint) throws Exception {
        super.open(checkpoint);

        _reader = new BufferedReader(new FileReader(Paths.get(inputFile).toFile()));
        // Read the first line to skip the header row
        _reader.readLine();
    }

    @Override
    public Object readItem() throws Exception {
        try{
            String line = _reader.readLine();
            return line;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
