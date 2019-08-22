package net.sourceforge.jaad.spi.javasound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import javax.sound.sampled.AudioFormat;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;

public class MP4AudioInputStream extends AsynchronousAudioInputStream {

	private final AudioTrack track;
	private final Decoder decoder;
	private final SampleBuffer sampleBuffer;
	private AudioFormat audioFormat;
	private byte[] saved;
	private final RandomAccessFile raFile;
	private final File file;

	static final String ERROR_MESSAGE_AAC_TRACK_NOT_FOUND = "movie does not contain any AAC track";

	MP4AudioInputStream(InputStream in, AudioFormat format, long length) throws IOException {
		super(in, format, length);
		//save to file and create a RandomAccessFile
		file = new File("/tmp/"+UUID.randomUUID());
		Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		raFile = new RandomAccessFile(file, "r");
		
		final MP4Container cont = new MP4Container(raFile);
		final Movie movie = cont.getMovie();
		final List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
		if(tracks.isEmpty()) throw new IOException(ERROR_MESSAGE_AAC_TRACK_NOT_FOUND);
		track = (AudioTrack) tracks.get(0);

		decoder = new Decoder(track.getDecoderSpecificInfo());
		sampleBuffer = new SampleBuffer();
	}

	@Override
	public AudioFormat getFormat() {
		if(audioFormat==null) {
			//read first frame
			decodeFrame();
			audioFormat = new AudioFormat(sampleBuffer.getSampleRate(), sampleBuffer.getBitsPerSample(), sampleBuffer.getChannels(), true, true);
			saved = sampleBuffer.getData();
		}
		
		return audioFormat;
	}

	public void execute() {
		if(saved==null) {
			decodeFrame();
			if(buffer.isOpen()) buffer.write(sampleBuffer.getData());
		}
		else {
			buffer.write(saved);
			saved = null;
		}
	}

	private void decodeFrame() {
		if(!track.hasMoreFrames()) {
			buffer.close();
			return;
		}
		try {
			final Frame frame = track.readNextFrame();
			if(frame==null) {
				buffer.close();
				return;
			}
			decoder.decodeFrame(frame.getData(), sampleBuffer);
		}
		catch(IOException e) {
			buffer.close();
			return;
		}
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		
		this.raFile.close();
		this.file.delete();
	}
}
