export type Song = {
    id: number;
    title: string;
    artist: string;
    album: string;
    url: string;
    artwork: string;
};
export type GetAllAudioOptions = {
    sortBy?: "TITLE" | "ARTIST" | "ALBUM" | "DATE";
    orderBy?: "ASC" | "DESC";
    limit?: number;
    offset?: number;
    artworkQuality?: number;
};
type AudioModuleType = {
    getAllAudio(options?: GetAllAudioOptions): Promise<Song[]>;
    getSongsByAlbum(): Promise<Song[]>;
    searchSongsByTitle(options: {
        title: string;
    }): Promise<Song[]>;
};
declare const _default: AudioModuleType;
export default _default;
